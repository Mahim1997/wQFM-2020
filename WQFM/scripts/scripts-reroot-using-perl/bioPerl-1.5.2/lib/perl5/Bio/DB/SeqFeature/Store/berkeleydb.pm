package Bio::DB::SeqFeature::Store::berkeleydb;

# $Id: berkeleydb.pm,v 1.5.4.5 2006/11/22 20:27:47 lstein Exp $


use strict;
use base 'Bio::DB::SeqFeature::Store';
use Bio::DB::GFF::Util::Rearrange 'rearrange';
use Bio::DB::Fasta;
use DB_File;
use Fcntl qw(O_RDWR O_CREAT);
use File::Temp 'tempdir';
use File::Path 'rmtree','mkpath';
use constant BINSIZE => 10_000;
use constant MININT  => -999_999_999_999;
use constant MAXINT  => 999_999_999_999;

=head1 NAME

Bio::DB::SeqFeature::Store::berkeleydb -- Storage and retrieval of sequence annotation data in Berkeleydb files

=head1 SYNOPSIS

  use Bio::DB::SeqFeature::Store;

  # Create a database from the feature files located in /home/fly4.3 and store
  # the database index in the same directory:
  $db =  Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                          -dir     => '/home/fly4.3');

  # Create a database that will monitor the files in /home/fly4.3, but store
  # the indexes in /var/databases/fly4.3
  $db      = Bio::DB::SeqFeature::Store->new( -adaptor    => 'berkeleydb',
                                              -dsn        => '/var/databases/fly4.3',
                                              -dir        => '/home/fly4.3');

  # Create a feature database from scratch
  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dsn     => '/var/databases/fly4.3',
                                             -create  => 1);

  # get a feature from somewhere
  my $feature = Bio::SeqFeature::Generic->new(...);

  # store it
  $db->store($feature) or die "Couldn't store!";

  # primary ID of the feature is changed to indicate its primary ID
  # in the database...
  my $id = $feature->primary_id;

  # get the feature back out
  my $f  = $db->fetch($id);

  # change the feature and update it
  $f->start(100);
  $db->update($f) or $self->throw("Couldn't update!");

  # use the GFF3 loader to do a bulk write:
  my $loader = Bio::DB::SeqFeature::Store::GFF3Loader->new(-store   => $db,
                                                           -verbose => 1,
                                                           -fast    => 1);
  $loader->load('/home/fly4.3/dmel-all.gff');


  # searching...
  # ...by id
  my @features = $db->fetch_many(@list_of_ids);

  # ...by name
  @features = $db->get_features_by_name('ZK909');

  # ...by alias
  @features = $db->get_features_by_alias('sma-3');

  # ...by type
  @features = $db->get_features_by_type('gene');

  # ...by location
  @features = $db->get_features_by_location(-seq_id=>'Chr1',-start=>4000,-end=>600000);

  # ...by attribute
  @features = $db->get_features_by_attribute({description => 'protein kinase'})

  # ...by the GFF "Note" field
  @result_list = $db->search_notes('kinase');

  # ...by arbitrary combinations of selectors
  @features = $db->features(-name => $name,
                            -type => $types,
                            -seq_id => $seqid,
                            -start  => $start,
                            -end    => $end,
                            -attributes => $attributes);

  # ...using an iterator
  my $iterator = $db->get_seq_stream(-name => $name,
                                     -type => $types,
                                     -seq_id => $seqid,
                                     -start  => $start,
                                     -end    => $end,
                                     -attributes => $attributes);

  while (my $feature = $iterator->next_seq) {
    # do something with the feature
  }

  # ...limiting the search to a particular region
  my $segment  = $db->segment('Chr1',5000=>6000);
  my @features = $segment->features(-type=>['mRNA','match']);

  # getting & storing sequence information
  # Warning: this returns a string, and not a PrimarySeq object
  $db->insert_sequence('Chr1','GATCCCCCGGGATTCCAAAA...');
  my $sequence = $db->fetch_sequence('Chr1',5000=>6000);

  # create a new feature in the database
  my $feature = $db->new_feature(-primary_tag => 'mRNA',
                                 -seq_id      => 'chr3',
                                 -start      => 10000,
                                 -end        => 11000);

=head1 DESCRIPTION

Bio::DB::SeqFeature::Store::berkeleydb is the Berkeleydb adaptor for
Bio::DB::SeqFeature::Store. You will not create it directly, but
instead use Bio::DB::SeqFeature::Store-E<gt>new() to do so.

See L<Bio::DB::SeqFeature::Store> for complete usage instructions.

=head2 Using the berkeleydb adaptor

The Berkeley database consists of a series of Berkeleydb index files,
and a couple of special purpose indexes. You can create the index
files from scratch by creating a new database and calling
new_feature() repeatedly, you can create the database and then bulk
populate it using the GFF3 loader, or you can monitor a directory of
preexisting GFF3 and FASTA files and rebuild the indexes whenever one
or more of the fiels changes. The last mode is probably the most
convenient.

=over 4

=item The new() constructor

The new() constructor method all the arguments recognized by
Bio::DB::SeqFeature::Store, and a few additional ones. 

Standard arguments:

 Name               Value
 ----               -----

 -adaptor           The name of the Adaptor class (default DBI::mysql)

 -serializer        The name of the serializer class (default Storable)

 -index_subfeatures Whether or not to make subfeatures searchable
                    (default true)

 -cache             Activate LRU caching feature -- size of cache

 -compress          Compresses features before storing them in database
                    using Compress::Zlib

Adaptor-specific arguments

 Name               Value
 ----               -----

 -dsn               Where the index files are stored

 -dir               Where the source (GFF3, FASTA) files are stored

 -autoindex         An alias for -dir.

 -write             Pass true to open the index files for writing.

 -create            Pass true to create the index files if they don't exist
                    (implies -write=>1)

 -temp              Pass true to create temporary index files that will
                    be deleted once the script exits.

Examples:

To create an empty database which will be populated using calls to
store() or new_feature(), or which will be bulk-loaded using the GFF3
loader:

  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dsn     => '/var/databases/fly4.3',
                                             -create  => 1);

To open a preexisting database in read-only mode:

  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dsn     => '/var/databases/fly4.3');

To open a preexisting database in read/write (update) mode:

  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dsn     => '/var/databases/fly4.3',
                                             -write   => 1);

To monitor a set of GFF3 and FASTA files located in a directory and
create/update the database indexes as needed. The indexes will be
stored in a new subdirectory named "indexes":

  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dir     => '/var/databases/fly4.3');

As above, but store the source files and index files in separate directories:

  $db     = Bio::DB::SeqFeature::Store->new( -adaptor => 'berkeleydb',
                                             -dsn     => '/var/databases/fly4.3',
                                             -dir     => '/home/gff3_files/fly4.3');

B<-autoindex> is an alias for B<-dir>.

=back

See L<Bio::DB::SeqFeature::Store> for all the access methods supported
by this adaptor. The various methods for storing and updating features
and sequences into the database are supported, but there is no
locking. If two processes try to update the same database
simultaneously, the database will likely become corrupted.

=cut

###
# object initialization
#
sub init {
  my $self          = shift;
  my ($directory,
      $autoindex,
      $is_temporary,
      $write,
      $create,
     ) = rearrange([['DSN','DB'],
		   [qw(DIR AUTOINDEX)],
		   ['TMP','TEMP','TEMPORARY'],
		   [qw(WRITE WRITABLE)],
		   'CREATE',
		  ],@_);
  if ($autoindex) {
    -d $autoindex or $self->throw("Invalid directory $autoindex");
    $directory ||= "$autoindex/indexes";
  }
  $directory ||= $is_temporary ? File::Spec->tmpdir : '.';
  # 
  my $pacname = __PACKAGE__;
  if ($^O =~ /mswin/i) {
    $pacname =~ s/:+/_/g;
  }
  $directory = tempdir($pacname.'_XXXXXX',
		       TMPDIR=>1,
		       CLEANUP=>1,
		       DIR=>$directory) if $is_temporary;
  mkpath($directory);
  -d $directory or $self->throw("Invalid directory $directory");

  $create++ if $is_temporary;
  $write ||= $create;
  $self->throw("Can't write into the directory $directory") 
    if $write && !-w $directory;


  $self->default_settings;
  $self->directory($directory);
  $self->temporary($is_temporary);
  $self->_delete_databases()    if $create;
  $self->_open_databases($write,$create,$autoindex);
  $self->_permissions($write,$create);
  return $self;
}

sub can_store_parentage { 1 }

sub post_init {
  my $self = shift;
  my ($autodir) = rearrange([['DIR','AUTOINDEX']],@_);
  return unless $autodir && -d $autodir;

  my $maxtime   = 0;

  opendir (my $D,$autodir) or $self->throw("Couldn't open directory $autodir for reading: $!");
  my @reindex;
  my $fasta_files_present;

  while (defined (my $node = readdir($D))) {
    next if $node =~ /^\./;
    my $path      = "$autodir/$node";
    next unless -f $path;

    # skip fasta files - the Bio::DB::Fasta module indexes them on its own
    if ($node =~ /\.(?:fa|fasta|dna)(?:\.gz)?$/) {
      $fasta_files_present++;
      next;
    }

    # skip index files
    next if $node =~ /\.(?:bdb|idx|index|stamp)/;

    # skip autosave files, etc
    next if $node =~ /^\#/;
    next if $node =~ /~$/;

    my $mtime = _mtime(\*_);  # not a typo
    $maxtime   = $mtime if $mtime > $maxtime;
    push @reindex,$path;
  }

  close $D;

  my $timestamp_time  = _mtime($self->_mtime_path) || 0;

  if ($maxtime > $timestamp_time) {
    warn "Reindexing... this may take a while.";
    $self->_permissions(1,1);
    $self->_close_databases();
    $self->_open_databases(1,1);
    require Bio::DB::SeqFeature::Store::GFF3Loader
      unless Bio::DB::SeqFeature::Store::GFF3Loader->can('new');
    my $loader = Bio::DB::SeqFeature::Store::GFF3Loader->new(-store    => $self,
							     -sf_class => $self->seqfeature_class) 
      or $self->throw("Couldn't create GFF3Loader");
    $loader->load(@reindex);
    $self->_touch_timestamp;
  }

  if ($fasta_files_present) {
    my $dna_db = Bio::DB::Fasta->new($autodir);
    $self->dna_db($dna_db);
  }
}

sub _open_databases {
  my $self = shift;
  my ($write,$create,$ignore_errors) = @_;

  my $directory  = $self->directory;
  unless (-d $directory) {  # directory does not exist
    $create or $self->throw("Directory $directory does not exist and you did not specify the -create flag");
    mkpath($directory) or $self->throw("Couldn't create database directory $directory: $!");
  }

  my $flags = O_RDONLY;
  $flags   |= O_RDWR  if $write;
  $flags   |= O_CREAT if $create;

  # Create the main database; this is a DB_HASH implementation
  my %h;
  my $result = tie (%h,'DB_File',$self->_features_path,$flags,0666,$DB_HASH);
  unless ($result) {
    return if $ignore_errors;  # autoindex set, so defer this
    $self->throw("Couldn't tie: ".$self->_features_path . " $!");
  }
  if ($create) {
    %h = ();
    $h{'.next_id'} = 1;
  }
  $self->db(\%h);

  # Create the index databases; these are DB_BTREE implementations with duplicates allowed.
  local $DB_BTREE->{flags} = R_DUP;
  $DB_BTREE->{compare}     = sub { lc($_[0]) cmp lc($_[1]) };
  for my $idx ($self->_index_files) {
    my $path = $self->_qualify("$idx.idx");
    my %db;
    tie(%db,'DB_File',$path,$flags,0666,$DB_BTREE)
      or $self->throw("Couldn't tie $path: $!");
    %db = () if $create;
    $self->index_db($idx=>\%db);
  }

  # Create the parentage database
  my %p;
  tie (%p,'DB_File',$self->_parentage_path,$flags,0666,$DB_BTREE)
    or $self->throw("Couldn't tie: ".$self->_parentage_path . $!);
    %p = () if $create;
  $self->parentage_db(\%p);

  if (-e $self->_fasta_path) {
    my $dna_db = Bio::DB::Fasta->new($self->_fasta_path) or $self->throw("Can't reindex sequence file: $@");
    $self->dna_db($dna_db);
  }

  my $mode =  $write  ? "+>>"
            : $create ? "+>"
            : "<";

  open (my $F,$mode,$self->_notes_path) or $self->throw($self->_notes_path.": $!");
  $self->notes_db($F);
}

sub commit { # reindex fasta files
  my $self = shift;
  if (my $fh = $self->{fasta_fh}) {
    $fh->close;
    $self->dna_db(Bio::DB::Fasta->new($self->{fasta_file}));
  } elsif (-d $self->directory) {
    $self->dna_db(Bio::DB::Fasta->new($self->directory));
  }
}

sub _close_databases {
  my $self = shift;
  $self->db(undef);
  $self->dna_db(undef);
  $self->notes_db(undef);
  $self->index_db($_=>undef) foreach $self->_index_files;
}

# do nothing -- new() with -create=>1 will do the trick
sub _init_database { }

sub _delete_databases {
  my $self = shift;
  for my $idx ($self->_index_files) {
    my $path = $self->_qualify("$idx.idx");
    unlink $path;
  }
  unlink $self->_parentage_path;
  unlink $self->_fasta_path;
  unlink $self->_features_path;
  unlink $self->_mtime_path;
}

sub _touch_timestamp {
  my $self = shift;
  my $tsf = $self->_mtime_path;
  open (F,">$tsf") or $self->throw("Couldn't open $tsf: $!");
  print F scalar(localtime);
  close F;
}

sub _store {
  my $self    = shift;
  my $indexed = shift;
  my $db   = $self->db;
  my $count = 0;
  for my $obj (@_) {
    my $primary_id = $obj->primary_id;
    $self->_delete_indexes($obj,$primary_id)  if $indexed && $primary_id;
    $primary_id    = $db->{'.next_id'}++ unless defined $primary_id;
    $db->{$primary_id} = $self->freeze($obj);
    $obj->primary_id($primary_id);
    $self->_update_indexes($obj)              if $indexed;
    $count++;
  }
  $count;
}

sub _delete_indexes {
  my $self = shift;
  my ($obj,$id) = @_;
  # the additional "1" causes the index to be deleted
  $self->_update_name_index($obj,$id,1);
  $self->_update_type_index($obj,$id,1);
  $self->_update_location_index($obj,$id,1);
  $self->_update_attribute_index($obj,$id,1);
  $self->_update_note_index($obj,$id,1);
}

sub _fetch {
  my $self = shift;
  my $id   = shift;
  my $db = $self->db;
  my $obj = $self->thaw($db->{$id},$id);
  $obj;
}

sub _add_SeqFeature {
  my $self = shift;
  my $parent   = shift;
  my @children = @_;
  my $parent_id = (ref $parent ? $parent->primary_id : $parent)
    or $self->throw("$parent should have a primary_id");
  my $p = $self->parentage_db;
  for my $child (@children) {
    my $child_id = ref $child ? $child->primary_id : $child;
    defined $child_id or $self->throw("no primary ID known for $child");
    $p->{$parent_id} = $child_id;
  }
}

sub _fetch_SeqFeatures {
  my $self   = shift;
  my $parent = shift;
  my @types  = @_;
  my $parent_id = $parent->primary_id or $self->throw("$parent should have a primary_id");
  my $index     = $self->parentage_db;
  my $db        = tied %$index;

  my @children_ids  = $db->get_dup($parent_id);
  my @children      = map {$self->fetch($_)} @children_ids;

  if (@types) {
    my $regexp = join '|',map {quotemeta($_)} $self->find_types(@types);
    return grep {($_->primary_tag.':'.$_->source_tag) =~ /^$regexp$/i} @children;
  } else {
    return @children;
  }
}

sub _update_indexes {
  my $self = shift;
  my $obj  = shift;
  defined (my $id   = $obj->primary_id) or return;
  $self->_update_name_index($obj,$id);
  $self->_update_type_index($obj,$id);
  $self->_update_location_index($obj,$id);
  $self->_update_attribute_index($obj,$id);
  $self->_update_note_index($obj,$id);
}

sub _update_name_index {
  my $self = shift;
  my ($obj,$id,$delete) = @_;
  my $db = $self->index_db('names') or $self->throw("Couldn't find 'names' index file");
  my ($names,$aliases) = $self->feature_names($obj);

  # little stinky - needs minor refactoring
  foreach (@$names) {
    my $key = lc $_;
    $self->update_or_delete($delete,$db,$key,$id);
  }

  foreach (@$aliases) {
    my $key = lc($_)."_2"; # the _2 indicates a secondary (alias) ID
    $self->update_or_delete($delete,$db,$key,$id);
  }

}

sub _update_type_index {
  my $self = shift;
  my ($obj,$id,$delete) = @_;
  my $db = $self->index_db('types')
    or $self->throw("Couldn't find 'types' index file");
  my $primary_tag = $obj->primary_tag;
  my $source_tag  = $obj->source_tag || '';
  return unless defined $primary_tag;

  $primary_tag    .= ":$source_tag";
  my $key          = lc $primary_tag;
  $self->update_or_delete($delete,$db,$key,$id);
}

# Note: this indexing scheme is space-inefficient because it stores the
# denormalized sequence ID followed by the bin in XXXXXX zero-leading
# format. It should be replaced with a binary numeric encoding and the
# BTREE {compare} attribute changed accordingly.
sub _update_location_index {
  my $self = shift;
  my ($obj,$id,$delete) = @_;
  my $db = $self->index_db('locations')
    or $self->throw("Couldn't find 'locations' index file");

  my $seq_id      = $obj->seq_id || '';
  my $start       = $obj->start  || '';
  my $end         = $obj->end    || '';
  my $strand      = $obj->strand;
  my $bin_min     = int $start/BINSIZE;
  my $bin_max     = int $end/BINSIZE;

  for (my $bin = $bin_min; $bin <= $bin_max; $bin++ ) {
    my $key = sprintf("%s%06d",lc($seq_id),$bin);
    $self->update_or_delete($delete,$db,$key,pack("i4",$id,$start,$end,$strand));
  }
}

sub _update_attribute_index {
  my $self      = shift;
  my ($obj,$id,$delete) = @_;
  my $db = $self->index_db('attributes')
    or $self->throw("Couldn't find 'attributes' index file");

  for my $tag ($obj->all_tags) {
    for my $value ($obj->each_tag_value($tag)) {
      my $key = "\L${tag}:${value}\E";
      $self->update_or_delete($delete,$db,$key,$id);
    }
  }
}

sub _update_note_index {
  my $self = shift;
  my ($obj,$id,$delete) = @_;
  return if $delete; # we don't know how to do this

  my $fh = $self->notes_db;
  my @notes = $obj->get_tag_values('Note') if $obj->has_tag('Note');


  print $fh $_,"\t",pack("u*",$id) or $self->throw("An error occurred while updating note index: $!")
    foreach @notes;
}

sub update_or_delete {
  my $self = shift;
  my ($delete,$db,$key,$id) = @_;
  if ($delete) {
    tied(%$db)->del_dup($key,$id);
  } else {
    $db->{$key} = $id;
  }
}

# these methods return pointers to....
# the database that stores serialized objects
sub db {
  my $self = shift;
  my $d = $self->setting('db');
  $self->setting(db=>shift) if @_;
  $d;
}

sub parentage_db {
  my $self = shift;
  my $d = $self->setting('parentage_db');
  $self->setting(parentage_db=>shift) if @_;
  $d;
}

# the Bio::DB::Fasta object
sub dna_db {
  my $self = shift;
  my $d = $self->setting('dna_db');
  $self->setting(dna_db=>shift) if @_;
  $d;
}

# the specialized notes database
sub notes_db {
  my $self = shift;
  my $d = $self->setting('notes_db');
  $self->setting(notes_db=>shift) if @_;
  $d;
}

# The indicated index berkeley db
sub index_db {
  my $self = shift;
  my $index_name = shift;
  my $d = $self->setting($index_name);
  $self->setting($index_name=>shift) if @_;
  $d;
}


sub _mtime {
  my $file = shift;
  my @stat = stat($file);
  return $stat[9];
}

# return names of all the indexes
sub _index_files {
  return qw(names types locations attributes);
}

# the directory in which we store our indexes
sub directory {
  my $self = shift;
  my $d = $self->setting('directory');
  $self->setting(directory=>shift) if @_;
  $d;
}

# flag indicating that we are a temporary database
sub temporary {
  my $self = shift;
  my $d = $self->setting('temporary');
  $self->setting(temporary=>shift) if @_;
  $d;
}

sub _permissions {
  my $self = shift;
  my $d = $self->setting('permissions') or return;
  if (@_) {
    my ($write,$create) = @_;
    $self->setting(permissions=>[$write,$create]);
  }
  @$d;
}

# file name utilities...
sub _qualify {
  my $self = shift;
  my $file = shift;
  return $self->directory .'/' . $file;
}

sub _features_path {
  shift->_qualify('features.bdb');
}

sub _parentage_path {
  shift->_qualify('parentage.bdb');
}

sub _type_path {
  shift->_qualify('types.idx');
}

sub _location_path {
  shift->_qualify('locations.idx');
}

sub _attribute_path {
  shift->_qualify('attributes.idx');
}

sub _notes_path {
  shift->_qualify('notes.idx');
}

sub _fasta_path {
  shift->_qualify('sequence.fa');
}

sub _mtime_path {
  shift->_qualify('mtime.stamp');
}

###########################################
# searching
###########################################

sub _features {
  my $self = shift;
  my ($seq_id,$start,$end,$strand,
      $name,$class,$allow_aliases,
      $types,
      $attributes,
      $range_type,
      $iterator
     ) = rearrange([['SEQID','SEQ_ID','REF'],'START',['STOP','END'],'STRAND',
		    'NAME','CLASS','ALIASES',
		    ['TYPES','TYPE','PRIMARY_TAG'],
		    ['ATTRIBUTES','ATTRIBUTE'],
		    'RANGE_TYPE',
		    'ITERATOR',
		   ],@_);

  my (@from,@where,@args,@group);
  $range_type ||= 'overlaps';

  my @result;
  unless (defined $name or defined $seq_id or defined $types or defined $attributes) {
    @result = grep {$_ ne '.next_id' } keys %{$self->db};
  }

  my %found = ();
  my $result = 1;

  if (defined($name)) {
    # hacky backward compatibility workaround
    undef $class if $class && $class eq 'Sequence';
    $name     = "$class:$name" if defined $class && length $class > 0;
    $result &&= $self->filter_by_name($name,$allow_aliases,\%found);
  }

  if (defined $seq_id) {
    $result &&= $self->filter_by_location($seq_id,$start,$end,$strand,$range_type,\%found);
  }

  if (defined $types) {
    $result &&= $self->filter_by_type($types,\%found);
  }

  if (defined $attributes) {
    $result &&= $self->filter_by_attribute($attributes,\%found);
  }

  push @result,keys %found if $result;
  return $iterator ? Bio::DB::SeqFeature::Store::berkeleydb::Iterator->new($self,\@result)
                   : map {$self->fetch($_)} @result;
}

sub filter_by_name {
  my $self = shift;
  my ($name,$allow_aliases,$filter) = @_;

  my $index = $self->index_db('names');
  my $db    = tied(%$index);

  my ($stem,$regexp) = $self->glob_match($name);
  $stem   ||= $name;
  $regexp ||= $name;
  $regexp .= "(?:_2)?" if $allow_aliases;

  my $key   = $stem;
  my $value;
  my @results;
  for (my $status = $db->seq($key,$value,R_CURSOR);
       $status == 0 and $key =~ /^$regexp$/i;
       $status = $db->seq($key,$value,R_NEXT)) {
    push @results,$value;
  }
  $self->update_filter($filter,\@results);
}

sub filter_by_type {
  my $self = shift;
  my ($types,$filter) = @_;
  my @types = ref $types eq 'ARRAY' ?  @$types : $types;

  my $index = $self->index_db('types');
  my $db    = tied(%$index);

  my @results;

  for my $type (@types) {
    my ($primary_tag,$source_tag);
    if (ref $type && $type->isa('Bio::DB::GFF::Typename')) {
      $primary_tag = $type->method;
      $source_tag  = $type->source;
    } else {
      ($primary_tag,$source_tag) = split ':',$type,2;
    }
    my $match = defined $source_tag ? "^$primary_tag:$source_tag\$" : "^$primary_tag:";
    $source_tag ||= '';
    my $key   = lc "$primary_tag:$source_tag";
    my $value;

    for (my $status = $db->seq($key,$value,R_CURSOR);
 	 $status == 0 && $key =~ /$match/i;
	 $status = $db->seq($key,$value,R_NEXT)) {
      push @results,$value;
    }

  }
  $self->update_filter($filter,\@results);
}

sub filter_by_location {
  my $self = shift;
  my ($seq_id,$start,$end,$strand,$range_type,$filter) = @_;
  $strand ||= 0;

  my $index    = $self->index_db('locations');
  my $db       = tied(%$index);

  my $binstart = defined $start ? sprintf("%06d",int $start/BINSIZE) : '';
  my $binend   = defined $end   ? sprintf("%06d",int $end/BINSIZE)   : 'z';  # beyond a number

  my %seenit;
  my @results;

  $start = MININT  if !defined $start;
  $end   = MAXINT  if !defined $end;

  if ($range_type eq 'overlaps' or $range_type eq 'contains') {
    my $key     = "\L$seq_id\E$binstart";
    my $keystop = "\L$seq_id\E$binend";
    my $value;
    for (my $status = $db->seq($key,$value,R_CURSOR);
	 $status == 0 && $key le $keystop;
	 $status = $db->seq($key,$value,R_NEXT)) {
      my ($id,$fstart,$fend,$fstrand) = unpack("i4",$value);
      next if $seenit{$id}++;
      next if $strand && $fstrand != $strand;
      if ($range_type eq 'overlaps') {
	next unless $fend >= $start && $fstart <= $end;
      }
      elsif ($range_type eq 'contains') {
	next unless $fstart >= $start && $fend <= $end;
      }
      push @results,$id;
    }
  }

  # for contained in, we look for features originating and terminating outside the specified range
  # this is incredibly inefficient, but fortunately the query is rare (?)
  elsif ($range_type eq 'contained_in') {
    my $key     = "\L$seq_id";
    my $keystop = "\L$seq_id\E$binstart";
    my $value;

    # do the left part of the range
    for (my $status = $db->seq($key,$value,R_CURSOR);
	 $status == 0 && $key le $keystop;
	 $status = $db->seq($key,$value,R_NEXT)) {
      my ($id,$fstart,$fend,$fstrand) = unpack("i4",$value);
      next if $seenit{$id}++;
      next if $strand && $fstrand != $strand;
      next unless $fstart <= $start && $fend >= $end;
      push @results,$id;
    }

    # do the right part of the range
    $key = "\L$seq_id\E$binend";
    for (my $status = $db->seq($key,$value,R_CURSOR);
	 $status == 0;
	 $status = $db->seq($key,$value,R_NEXT)) {
      my ($id,$fstart,$fend,$fstrand) = unpack("i4",$value);
      next if $seenit{$id}++;
      next if $strand && $fstrand != $strand;
      next unless $fstart <= $start && $fend >= $end;
      push @results,$id;
    }

  }

  $self->update_filter($filter,\@results);
}

sub filter_by_attribute {
  my $self = shift;
  my ($attributes,$filter) = @_;

  my $index = $self->index_db('attributes');
  my $db    = tied(%$index);
  my $result;

  for my $att_name (keys %$attributes) {
    my @result;
    my @search_terms = ref($attributes->{$att_name}) && ref($attributes->{$att_name}) eq 'ARRAY'
                           ? @{$attributes->{$att_name}} : $attributes->{$att_name};

    for my $v (@search_terms) {
      my ($stem,$regexp) = $self->glob_match($v);
      $stem   ||= $v;
      $regexp ||= $v;
      my $key = "\L${att_name}:${stem}\E";
      my $value;
      for (my $status = $db->seq($key,$value,R_CURSOR);
	   $status == 0 && $key =~ /^$att_name:$regexp$/i;
	   $status = $db->seq($key,$value,R_NEXT)) {
	push @result,$value;
      }
    }
    $result ||= $self->update_filter($filter,\@result);
  }
  $result;
}

sub _search_attributes {
  my $self = shift;
  my ($search_string,$attribute_array,$limit) = @_;
  $search_string =~ tr/*?//d;
  my @words = map {quotemeta($_)} $search_string =~ /(\w+)/g;
  my $search = join '|',@words;

  my $index = $self->index_db('attributes');
  my $db    = tied(%$index);

  my (%results,%notes);

  for my $tag (@$attribute_array) {
    my $id;
    my $key = "\L$tag:\E";
    for (my $status = $db->seq($key,$id,R_CURSOR);
	 $status == 0 and $key =~ /^$tag:(.*)/i;
	 $status = $db->seq($key,$id,R_NEXT)) {
      my $text = $1;
      next unless $text =~ /$search/;
      for my $w (@words) {
	my @hits = $text =~ /($w)/ig or next;
	$results{$id} += @hits;
      }
      $notes{$id} .= "$text ";
    }
  }

  my @results;
  for my $id (keys %results) {
    my $hits = $results{$id};
    my $note = $notes{$id};
    $note =~ s/\s+$//;
    my $relevance = 10 * $hits;
    my $feature   = $self->fetch($id) or next;
    my $name      = $feature->display_name or next;
    push @results,[$name,$note,$relevance];
  }

  return @results;
}

sub search_notes {
  my $self = shift;
  my ($search_string,$limit) = @_;

  $search_string =~ tr/*?//d;

  my @results;

  my @words = map {quotemeta($_)} $search_string =~ /(\w+)/g;
  my $search = join '|',@words;

  my (%found,$found);
  my $note_index = $self->notes_db;
  seek($note_index,0,0);  # back to start
  while (<$note_index>) {
    next unless /$search/;
    chomp;
    my ($note,$uu) = split "\t";
    $found{unpack("u*",$uu)}++;
    last if $limit && ++$found >= $limit;
  }

  my (@features, @matches);
  for my $idx (keys %found) {
    my $feature    = $self->fetch($idx) or next;
    my @values     = $feature->get_tag_values('Note') if $feature->has_tag('Note');
    my $value      = "@values";

    my $hits;
    $hits++ while $value =~ /($search)/ig;  # count the number of times we were hit
    push @matches,$hits;
    push @features,$feature;
  }

  for (my $i=0; $i<@matches; $i++)  {
    my $feature = $features[$i];
    my $matches = $matches[$i];

    my $relevance = 10 * $matches;
    my $note;
    $note   = join ' ',$feature->get_tag_values('Note') if $feature->has_tag('Note');
    push @results,[$feature->display_name,$note,$relevance];
  }

  return @results;
}

sub glob_match {
  my $self = shift;
  my $term = shift;
  return unless $term =~ /([^*?]*)(?:^|[^\\])?[*?]/;
  my $stem = $1;
  $term =~ s/(^|[^\\])([+\[\]^{}\$|\(\).])/$1\\$2/g;
  $term =~ s/(^|[^\\])\*/$1.*/g;
  $term =~ s/(^|[^\\])\?/$1./g;
  return ($stem,$term);
}


sub update_filter {
  my $self = shift;
  my ($filter,$results) = @_;
  return unless @$results;

  if (%$filter) {
    my @filtered = grep {$filter->{$_}} @$results;
    %$filter     = map {$_=>1} @filtered;
  } else {
    %$filter     = map {$_=>1} @$results;
  }

}

# this is ugly
sub _insert_sequence {
  my $self = shift;
  my ($seqid,$seq,$offset) = @_;
  my $dna_fh = $self->private_fasta_file or return;
  if ($offset == 0) { # start of the sequence
    print $dna_fh ">$seqid\n";
  }
  print $dna_fh $seq,"\n";
}

sub _fetch_sequence {
  my $self = shift;
  my ($seqid,$start,$end) = @_;
  my $db = $self->dna_db or return;
  return $db->seq($seqid,$start,$end);
}

sub private_fasta_file {
  my $self = shift;
  return $self->{fasta_fh} if exists $self->{fasta_fh};
  $self->{fasta_file}   = $self->_qualify("sequence.fa");
  return $self->{fasta_fh} = IO::File->new($self->{fasta_file},">");
}

sub finish_bulk_update {
  my $self = shift;
  if (my $fh = $self->{fasta_fh}) {
    $fh->close;
    $self->{fasta_db} = Bio::DB::Fasta->new($self->{fasta_file});
  }
}


sub DESTROY {
  my $self = shift;
  $self->_close_databases();
  rmtree($self->directory,0,1) if $self->temporary;
}

# TIE interface -- a little annoying because we are storing magic ".variable"
# meta-variables in the same data structure as the IDs, so these variables
# must be skipped.
sub _firstid {
  my $self  = shift;
  my $db    = $self->db;
  my ($key,$value);
  while ( ($key,$value) = each %{$db}) {
    last unless $key =~ /^\./;
  }
  $key;
}

sub _nextid {
  my $self = shift;
  my $id   = shift;
  my $db    = $self->db;
  my ($key,$value);
  while ( ($key,$value) = each %$db) {
    last unless $key =~ /^\./;
  }
  $key;
}

sub _existsid {
  my $self = shift;
  my $id   = shift;
  return exists $self->db->{$id};
}

sub _deleteid {
  my $self = shift;
  my $id   = shift;
  my $obj  = $self->fetch($id) or return;
  $self->_delete_indexes($obj,$id);
  delete $self->db->{$id};
}

sub _clearall {
  my $self = shift;
  $self->_close_databases();
  $self->_delete_databases();
  my ($write,$create) = $self->_permissions;
  $self->_open_databases($write,$create);
}

sub _featurecount {
  my $self = shift;
  return scalar %{$self->db};
}


package Bio::DB::SeqFeature::Store::berkeleydb::Iterator;

sub new {
  my $class = shift;
  my $store = shift;
  my $ids   = shift;
  return bless {store => $store,
		ids   => $ids},ref($class) || $class;
}

sub next_seq {
  my $self  = shift;
  my $store = $self->{store} or return;
  my $id    = shift @{$self->{ids}};
  defined $id or return;
  return $store->fetch($id);
}

1;

__END__

=head1 BUGS

This is an early version, so there are certainly some bugs. Please
use the BioPerl bug tracking system to report bugs.

=head1 SEE ALSO

L<bioperl>,
L<Bio::DB::SeqFeature>,
L<Bio::DB::SeqFeature::Store>,
L<Bio::DB::SeqFeature::GFF3Loader>,
L<Bio::DB::SeqFeature::Segment>,
L<Bio::DB::SeqFeature::Store::memory>,
L<Bio::DB::SeqFeature::Store::DBI::mysql>,

=head1 AUTHOR

Lincoln Stein E<lt>lstein@cshl.orgE<gt>.

Copyright (c) 2006 Cold Spring Harbor Laboratory.

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl itself.

=cut

