# $Id: Annotated.pm,v 1.34.4.3 2006/10/17 09:12:57 sendu Exp $
#
# BioPerl module for Bio::SeqFeature::Annotated
#
# Cared for by Allen Day <allenday at ucla.edu>
#
# Copyright Allen Day
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::SeqFeature::Annotated - PLEASE PUT SOMETHING HERE

=head1 SYNOPSIS

    # none yet, complain to authors

=head1 DESCRIPTION

None yet, complain to authors.

=head1 Implemented Interfaces

This class implementes the following interfaces.

=over 4

=item Bio::SeqFeatureI

Note that this includes implementing Bio::RangeI.

=item Bio::AnnotatableI

=item Bio::FeatureHolderI

Features held by a feature are essentially sub-features.

=back

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to one
of the Bioperl mailing lists.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.  Bug reports can be submitted via 
the web:

  http://bugzilla.open-bio.org/

=head1 AUTHOR - Allen Day

Allen Day E<lt>allenday at ucla.eduE<gt>

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut


package Bio::SeqFeature::Annotated;

use strict;

use Bio::Annotation::Collection;
use Bio::Annotation::OntologyTerm;
use Bio::Annotation::Target;
use Bio::LocatableSeq;
use Bio::Location::Simple;
use Bio::Ontology::OntologyStore;
use Bio::Tools::GFF;

use URI::Escape;

use base qw(Bio::Root::Root Bio::SeqFeatureI Bio::AnnotatableI Bio::FeatureHolderI);

######################################
#get_SeqFeatures
#display_name
#primary_tag
#source_tag                       x with warning
#has_tag
#get_tag_values
#get_tagset_values
#get_all_tags
#attach_seq
#seq                              x
#entire_seq                       x
#seq_id
#gff_string
#_static_gff_handler
#start                            x
#end                              x
#strand                           x
#location
#primary_id

sub new {
    my ( $caller, @args) = @_;
    my ($self) = $caller->SUPER::new(@args); 

    $self->_initialize(@args);

    return $self;
}

sub _initialize {
  my ($self,@args) = @_;
  my ($start, $end, $strand, $frame, $phase, $score,
      $name, $annot, $location,
      $display_name, # deprecate
      $seq_id, $type,$source,$feature
     ) =
        $self->_rearrange([qw(START
                              END
                              STRAND
                              FRAME
                              PHASE
                              SCORE
                              NAME
                              ANNOTATION
                              LOCATION
                              DISPLAY_NAME
                              SEQ_ID
                              TYPE
                              SOURCE
			      FEATURE
                             )], @args);
  defined $start        && $self->start($start);
  defined $end          && $self->end($end);
  defined $strand       && $self->strand($strand);
  defined $frame        && $self->frame($frame);
  defined $phase        && $self->phase($phase);
  defined $score        && $self->score($score);
  defined $source       && $self->source($source);
  defined $type         && $self->type($type);
  defined $location     && $self->location($location);
  defined $annot        && $self->annotation($annot);
  defined $feature      && $self->from_feature($feature);

  if( defined($display_name) && defined($name) ){
	  $self->throw('Cannot define (-id and -seq_id) or (-name and -display_name) attributes');
  }
  defined $seq_id                   && $self->seq_id($seq_id);
  defined ($name || $display_name)  && $self->name($name || $display_name);
}

=head1 ATTRIBUTE ACCESSORS FOR Bio::SeqFeature::Annotated

=cut

=head2 from_feature

  Usage: $obj->from_feature($myfeature);
  Desc : initialize this object with the contents of another feature
         object.  Useful for converting objects like
         L<Bio::SeqFeature::Generic> to this class
  Ret  : nothing meaningful
  Args : a single object of some other feature type,
  Side Effects: throws error on failure
  Example:

=cut

sub from_feature {
  my ($self,$feat,%opts) = @_;

  ref($feat) && ($feat->isa('Bio::AnnotationCollectionI') || $feat->isa('Bio::SeqFeatureI'))
    or $self->throw('invalid arguments to from_feature');

  #TODO: add overrides in opts for these values, so people don't have to screw up their feature object
  #if they don't want to

  ### set most of the data
  foreach my $fieldname (qw/ start end strand frame score location seq_id source_tag primary_tag/) {
    no strict 'refs'; #using symbolic refs
    $self->$fieldname( $feat->$fieldname );
  }

  ### now pick up the annotations/tags of the other feature
  #for Bio::AnnotationCollectionI features
  if ( $feat->isa('Bio::AnnotatableI') ) {
    foreach my $key ( $feat->annotation->get_all_annotation_keys() ) {
      my @values = $feat->annotation->get_Annotations($key);
      @values = _aggregate_scalar_annotations(\%opts,$key,@values);
      foreach my $val (@values) {
	$self->add_Annotation($key,$val)
      }
    }
  }
}
#given a key and its values, make the values into
#Bio::Annotation::\w+ objects
sub _aggregate_scalar_annotations {
  my ($opts,$key,@values) = @_;

  #anything that's not an object, make it a SimpleValue
  @values = map { ref($_) ? $_ : Bio::Annotation::SimpleValue->new(-value => $_) } @values;

  #try to make Target objects
  if($key eq 'Target' && (@values == 3 || @values == 4)
     && @values == grep {$_->isa('Bio::Annotation::SimpleValue')} @values
    ) {
    @values = map {$_->value} @values;
    #make a strand if it doesn't have one, enforcing start <= end
    if(@values == 3) {
      if($values[1] <= $values[2]) {
	$values[3] = '+';
      } else {
	@values[1,2] = @values[2,1];
	$values[3] = '-';
      }
    }
    return ( Bio::Annotation::Target->new( -target_id => $values[0],
					   -start     => $values[1],
					   -end       => $values[2],
					   -strand    => $values[3],
					 )
	   );
  }
  #try to make DBLink objects
  elsif($key eq 'dblink' || $key eq 'Dbxref') {
    return map {
      if( /:/ ) { #convert to a DBLink if it has a colon in it
	my ($db,$id) = split /:/,$_->value;
	Bio::Annotation::DBLink->new( -database   => $db,
				      -primary_id => $id,
				    );
      } else { #otherwise leave as a SimpleValue
	$_
      }
    } @values;
  }
  #make OntologyTerm objects
  elsif($key eq 'Ontology_term') {
    return map { Bio::Annotation::OntologyTerm->new(-identifier => $_->value) } @values
  }
  #make Comment objects
  elsif($key eq 'comment') {
    return map { Bio::Annotation::Comment->new( -text => $_->value ) } @values;
  }

  return @values;
}


=head2 seq_id()

 Usage   : $obj->seq_id($newval)
 Function: holds a string corresponding to the unique
           seq_id of the sequence underlying the feature
           (e.g. database accession or primary key).
 Returns : a Bio::Annotation::SimpleValue object representing the seq_id.
 Args    : on set, some string or a Bio::Annotation::SimpleValue object.

=cut

sub seq_id {
  my($self,$val) = @_;
  if (defined($val)) {
      my $term = undef;
      if (!ref($val)) {
	  $term = Bio::Annotation::SimpleValue->new(-value => uri_unescape($val));
      } elsif (ref($val) && $val->isa('Bio::Annotation::SimpleValue')) {
	  $term = $val;
      }
      if (!defined($term) || ($term->value =~ /^>/)) {
	  $self->throw('give seq_id() a scalar or Bio::Annotation::SimpleValue object, not '.$val);
      }
      $self->remove_Annotations('seq_id');
      $self->add_Annotation('seq_id', $term);
  }

  $self->seq_id('.') unless ($self->get_Annotations('seq_id')); # make sure we always have something

  return $self->get_Annotations('seq_id');
}

=head2 name()

 Usage   : $obj->name($newval)
 Function: human-readable name for the feature.
 Returns : value of name (a scalar)
 Args    : on set, new value (a scalar or undef, optional)

=cut

sub name {
  my($self,$val) = @_;
  $self->{'name'} = $val if defined($val);
  return $self->{'name'};
}

=head2 type()

 Usage   : $obj->type($newval)
 Function: a SOFA type for the feature.
 Returns : Bio::Annotation::OntologyTerm object representing the type.
 Args    : on set, a SOFA name, identifier, or Bio::Annotation::OntologyTerm object.

=cut

use constant MAX_TYPE_CACHE_MEMBERS => 20;
sub type {
  my($self,$val) = @_;
  if(defined($val)){
    # print("Trying to set annotated->type to $val\n");
    my $term = undef;

    if(!ref($val)){
      #we have a plain text annotation coming in.  try to map it to SOFA.

      our %__type_cache; #a little cache of plaintext types we've already seen

      #clear our cache if it gets too big
      if(scalar(keys %__type_cache) > MAX_TYPE_CACHE_MEMBERS) {
	%__type_cache = ();
      }

      #set $term to either a cached value, or look up a new one, throwing
      #up if not found
      $term = $__type_cache{$val} ||= do {
	my $sofa = Bio::Ontology::OntologyStore->get_instance->get_ontology('Sequence Ontology Feature Annotation');
	my ($soterm) = $val =~ /^\D+:\d+$/ #does it look like an ident?
	  ? ($sofa->find_terms(-identifier => $val))[0] #yes, lookup by ident
	  : ($sofa->find_terms(-name => $val))[0];      #no, lookup by name
	
	#throw up if it's not in SOFA
	unless($soterm){
	  $self->throw("couldn't find a SOFA term matching type '$val'.");
	}
	my $newterm = Bio::Annotation::OntologyTerm->new;
	$newterm->term($soterm);
	$newterm;
      };
    }
    elsif(ref($val) && $val->isa('Bio::Annotation::OntologyTerm')){
      $term = $val;
    }
    else {
      #we have the wrong type of object
      $self->throw('give type() a SOFA term name, identifier, or Bio::Annotation::OntologyTerm object, not '.$val);
    }
    $self->remove_Annotations('type');
    $self->add_Annotation('type',$term);
  }
  else {
    return $self->get_Annotations('type');
  }
}

=head2 source()

 Usage   : $obj->source($newval)
 Function: holds a string corresponding to the source of the feature.
 Returns : a Bio::Annotation::SimpleValue object representing the source.
 Args    : on set, some scalar or a Bio::Annotation::SimpleValue object.

=cut

sub source {
  my($self,$val) = @_;

  if (defined($val)) {
      my $term;
      if (!ref($val)) {
	  $term = Bio::Annotation::SimpleValue->new(-value => uri_unescape($val));
      } elsif (ref($val) && $val->isa('Bio::Annotation::SimpleValue')) {
	  $term = $val;
      } else {
	  $self->throw('give source() a scalar or Bio::Annotation::SimpleValue object, not '.$val);
      }
      $self->remove_Annotations('source');
      $self->add_Annotation('source', $term);
     
  }
  else {
    if (!$self->get_Annotations('source')) {
        $self->source('.');
    }
    return $self->get_Annotations('source');
  }
}

=head2 score()

 Usage   : $score = $feat->score()
           $feat->score($score)
 Function: holds a value corresponding to the score of the feature.
 Returns : a Bio::Annotation::SimpleValue object representing the score.
 Args    : on set, a scalar or a Bio::Annotation::SimpleValue object.

=cut

sub score {
  my $self = shift;
  my $val = shift;

  if(defined($val)){
      my $term = undef;
      if (!ref($val)) {
	  $term = Bio::Annotation::SimpleValue->new(-value => $val);
      } elsif (ref($val) && $val->isa('Bio::Annotation::SimpleValue')) {
	  $term = $val;
      }

      if ($term->value ne '.' &&
           (!defined($term) || ($term->value !~ /^[+-]?\d+\.?\d*(e-\d+)?/))) {
	  $self->throw("'$val' is not a valid score");
      }
      $self->remove_Annotations('score');
      $self->add_Annotation('score', $term);
  }

  $self->score('.') unless ($self->get_Annotations('score')); # make sure we always have something
  
  return $self->get_Annotations('score');
}

=head2 phase()

 Usage   : $phase = $feat->phase()
           $feat->phase($phase)
 Function: get/set on phase information
 Returns : a Bio::Annotation::SimpleValue object holdig one of 0,1,2,'.'
           as its value.
 Args    : on set, one of 0,1,2,'.' or a Bio::Annotation::SimpleValue
           object holding one of 0,1,2,'.' as its value.

=cut

sub phase {
  my $self = shift;
  my $val = shift;

  if(defined($val)){
      my $term = undef;
      if (!ref($val)) {
	  $term = Bio::Annotation::SimpleValue->new(-value => $val);
      } elsif (ref($val) && $val->isa('Bio::Annotation::SimpleValue')) {
	  $term = $val;
      }
      if (!defined($term) || ($term->value !~ /^[0-2.]$/)) {
	  $self->throw("'$val' is not a valid phase");
      }
      $self->remove_Annotations('phase');
      $self->add_Annotation('phase', $term);
  }

  $self->phase('.') unless (defined $self->get_Annotations('phase')); # make sure we always have something
  
  return $self->get_Annotations('phase');
}


=head2 frame()

 Usage   : $frame = $feat->frame()
           $feat->frame($phase)
 Function: get/set on phase information
 Returns : a Bio::Annotation::SimpleValue object holdig one of 0,1,2,'.'
           as its value.
 Args    : on set, one of 0,1,2,'.' or a Bio::Annotation::SimpleValue
           object holding one of 0,1,2,'.' as its value.

=cut

sub frame {
  my $self = shift;
  my $val = shift;

  if(defined($val)){
      my $term = undef;
      if (!ref($val)) {
	  $term = Bio::Annotation::SimpleValue->new(-value => $val);
      } elsif (ref($val) && $val->isa('Bio::Annotation::SimpleValue')) {
	  $term = $val;
      }
      if (!defined($term) || ($term->value !~ /^[0-2.]$/)) {
	  $self->throw("'$val' is not a valid frame");
      }
      $self->remove_Annotations('frame');
      $self->add_Annotation('frame', $term);
  }

  $self->frame('.') unless ($self->get_Annotations('frame')); # make sure we always have something
  
  return $self->get_Annotations('frame');
}

############################################################

=head1 SHORTCUT METHDODS TO ACCESS Bio::AnnotatableI INTERFACE METHODS

=cut

=head2 add_Annotation()

 Usage   :
 Function: $obj->add_Annotation() is a shortcut to $obj->annotation->add_Annotation
 Returns : 
 Args    :

=cut

sub add_Annotation {
  my ($self,@args) = @_;
  return $self->annotation->add_Annotation(@args);
}

=head2 remove_Annotations()

 Usage   :
 Function: $obj->remove_Annotations() is a shortcut to $obj->annotation->remove_Annotations
 Returns : 
 Args    :

=cut

sub remove_Annotations {
  my ($self,@args) = @_;
  return $self->annotation->remove_Annotations(@args);
}

############################################################

=head1 INTERFACE METHODS FOR Bio::SeqFeatureI

=cut

=head2 display_name()

 Deprecated, use L<Bio::SeqFeatureI/name()>.  Will raise a warning.

=cut

sub display_name {
  my $self = shift;

  #1.6
  #$self->warn('display_name() is deprecated, use name()');

  return $self->name(@_);
}

=head2 primary_tag()

 Deprecated, use L<Bio::SeqFeatureI/type()>.  Will raise a warning.

=cut

sub primary_tag {
  my $self = shift;

  #1.6
  #$self->warn('primary_tag() is deprecated, use type()');
  my $t = $self->type(@_);
  return ref($t) ? $t->name : $t;
}

=head2 source_tag()

 Deprecated, use L<Bio::SeqFeatureI/source()>.  Will raise a warning.

=cut

sub source_tag {
  my $self = shift;

  #1.6
  #$self->warn('source_tag() is deprecated, use source()');

  return $self->source(@_);
}


=head2 attach_seq()

 Usage   : $sf->attach_seq($seq)
 Function: Attaches a Bio::Seq object to this feature. This
           Bio::Seq object is for the *entire* sequence: ie
           from 1 to 10000
 Returns : TRUE on success
 Args    : a Bio::PrimarySeqI compliant object

=cut

sub attach_seq {
   my ($self, $seq) = @_;

   if ( ! ($seq && ref($seq) && $seq->isa("Bio::PrimarySeqI")) ) {
       $self->throw("Must attach Bio::PrimarySeqI objects to SeqFeatures");
   }

   $self->{'seq'} = $seq;

   # attach to sub features if they want it
   foreach ( $self->get_SeqFeatures() ) {
       $_->attach_seq($seq);
   }
   return 1;
}

=head2 seq()

 Usage   : $tseq = $sf->seq()
 Function: returns a truncated version of seq() with bounds matching this feature
 Returns : sub seq (a Bio::PrimarySeqI compliant object) on attached sequence
           bounded by start & end, or undef if there is no sequence attached
 Args    : none

=cut

sub seq {
  my ($self) = @_;

  return unless defined($self->entire_seq());

  my $seq = $self->entire_seq->trunc($self->start(), $self->end());

  if ( defined $self->strand && $self->strand == -1 ) {
    $seq = $seq->revcom;
  }

  return $seq;
}

=head2 entire_seq()

 Usage   : $whole_seq = $sf->entire_seq()
 Function: gives the entire sequence that this seqfeature is attached to
 Returns : a Bio::PrimarySeqI compliant object, or undef if there is no
           sequence attached
 Args    : none

=cut

sub entire_seq {
  return shift->{'seq'};
}

=head2 has_tag()

 See Bio::AnnotatableI::has_tag().

=cut

#implemented in Bio::AnnotatableI

# sub has_tag {
#   return shift->annotation->has_tag(@_);
# }

=head2 add_tag_value()

 See Bio::AnnotatableI::add_tag_value().

=cut

#implemented in Bio::AnnotatableI

# sub add_tag_value {
#   return shift->annotation->add_tag_value(@_);
# }

=head2 get_tag_values()

 See Bio::AnnotationCollectionI::get_tag_values().

=cut

#implemented in Bio::AnnotatableI

# sub get_tag_values {
#   return shift->annotation->get_tag_values(@_);
# }

=head2 get_all_tags()

 See Bio::AnnotationCollectionI::get_all_annotation_keys().

=cut

#implemented in Bio::AnnotatableI

# sub get_all_tags {
#   return shift->annotation->get_all_annotation_keys(@_);
# }

=head2 remove_tag()

 See Bio::AnnotationCollectionI::remove_tag().

=cut

#implemented in Bio::AnnotatableI

# sub remove_tag {
#   return shift->annotation->remove_tag(@_);
# }


############################################################

=head1 INTERFACE METHODS FOR Bio::RangeI

 as inherited via Bio::SeqFeatureI

=cut

=head2 length()

 Usage   : $feature->length()
 Function: Get the feature length computed as $feat->end - $feat->start + 1
 Returns : integer
 Args    : none

=cut

sub length {
  my $self = shift;
  return $self->end() - $self->start() + 1;
}

=head2 start()

 Usage   : $obj->start($newval)
 Function: Get/set on the start coordinate of the feature
 Returns : integer
 Args    : on set, new value (a scalar or undef, optional)

=cut

sub start {
  my ($self,$value) = @_;
  return $self->location->start($value);
}

=head2 end()

 Usage   : $obj->end($newval)
 Function: Get/set on the end coordinate of the feature
 Returns : integer
 Args    : on set, new value (a scalar or undef, optional)

=cut

sub end {
  my ($self,$value) = @_;
  return $self->location->end($value);
}

=head2 strand()

 Usage   : $strand = $feat->strand($newval)
 Function: get/set on strand information, being 1,-1 or 0
 Returns : -1,1 or 0
 Args    : ???

=cut

sub strand {
  my $self = shift;
  return $self->location->strand(@_);
}


############################################################

=head1 INTERFACE METHODS FOR Bio::FeatureHolderI

This includes methods for retrieving, adding, and removing
features. Since this is already a feature, features held by this
feature holder are essentially sub-features.

=cut

=head2 get_SeqFeatures

 Usage   : @feats = $feat->get_SeqFeatures();
 Function: Returns an array of Bio::SeqFeatureI objects
 Returns : An array
 Args    : none

=cut

sub get_SeqFeatures {
  return @{ shift->{'sub_array'} || []};
}

=head2 add_SeqFeature()

 Usage   : $feat->add_SeqFeature($subfeat);
           $feat->add_SeqFeature($subfeat,'EXPAND')
 Function: adds a SeqFeature into the subSeqFeature array.
           with no 'EXPAND' qualifer, subfeat will be tested
           as to whether it lies inside the parent, and throw
           an exception if not.

           If EXPAND is used, the parent''s start/end/strand will
           be adjusted so that it grows to accommodate the new
           subFeature
 Example :
 Returns : nothing
 Args    : a Bio::SeqFeatureI object

=cut

sub add_SeqFeature {
  my ($self,$val, $expand) = @_;

  return unless $val;

  if ((!ref($val)) || !$val->isa('Bio::SeqFeatureI') ) {
      $self->throw((ref($val) ? ref($val) : $val)
                   ." does not implement Bio::SeqFeatureI.");
  }

  if($expand && ($expand eq 'EXPAND')) {
      $self->_expand_region($val);
  } else {
      if ( !$self->contains($val) ) {
	  $self->warn("$val is not contained within parent feature, and expansion is not valid, ignoring.");
	  return;
      }
  }

  push(@{$self->{'sub_array'}},$val);
}

=head2 remove_SeqFeatures()

 Usage   : $obj->remove_SeqFeatures
 Function: Removes all sub SeqFeatures.  If you want to remove only a subset,
           remove that subset from the returned array, and add back the rest.
 Returns : The array of Bio::SeqFeatureI implementing sub-features that was
           deleted from this feature.
 Args    : none

=cut

sub remove_SeqFeatures {
  my ($self) = @_;

  my @subfeats = @{$self->{'sub_array'} || []};
  $self->{'sub_array'} = []; # zap the array.
  return @subfeats;
}

############################################################

=head1 INTERFACE METHODS FOR Bio::AnnotatableI

=cut

=head2 annotation()

 Usage   : $obj->annotation($annot_obj)
 Function: Get/set the annotation collection object for annotating this
           feature.
 Returns : A Bio::AnnotationCollectionI object
 Args    : newvalue (optional)

=cut

sub annotation {
    my ($obj,$value) = @_;

    # we are smart if someone references the object and there hasn't been
    # one set yet
    if(defined $value || ! defined $obj->{'annotation'} ) {
        $value = new Bio::Annotation::Collection unless ( defined $value );
        $obj->{'annotation'} = $value;
    }
    return $obj->{'annotation'};
}

############################################################

=head2 location()

 Usage   : my $location = $seqfeature->location()
 Function: returns a location object suitable for identifying location 
           of feature on sequence or parent feature  
 Returns : Bio::LocationI object
 Args    : [optional] Bio::LocationI object to set the value to.

=cut

sub location {
  my($self, $value ) = @_;

  if (defined($value)) {
    unless (ref($value) and $value->isa('Bio::LocationI')) {
      $self->throw("object $value pretends to be a location but ".
                   "does not implement Bio::LocationI");
    }
    $self->{'location'} = $value;
  }
  elsif (! $self->{'location'}) {
    # guarantees a real location object is returned every time
    $self->{'location'} = Bio::Location::Simple->new();
  }
  return $self->{'location'};
}

=head2 add_target()

 Usage   : $seqfeature->add_target(Bio::LocatableSeq->new(...));
 Function: adds a target location on another reference sequence for this feature
 Returns : true on success
 Args    : a Bio::LocatableSeq object

=cut

sub add_target {
  my ($self,$seq) = @_;
  $self->throw("$seq is not a Bio::LocatableSeq, bailing out") unless ref($seq) and seq->isa('Bio::LocatableSeq');
  push @{ $self->{'targets'} }, $seq;
  return $seq;
}

=head2 each_target()

 Usage   : @targets = $seqfeature->each_target();
 Function: Returns a list of Bio::LocatableSeqs which are the locations of this object.
           To obtain the "primary" location, see L</location()>.
 Returns : a list of 0..N Bio::LocatableSeq objects
 Args    : none

=cut

sub each_target {
  my ($self) = @_;
  return $self->{'targets'} ? @{ $self->{'targets'} } : ();
}

=head2 _expand_region

 Title   : _expand_region
 Usage   : $self->_expand_region($feature);
 Function: Expand the total region covered by this feature to
           accomodate for the given feature.

           May be called whenever any kind of subfeature is added to this
           feature. add_SeqFeature() already does this.
 Returns : 
 Args    : A Bio::SeqFeatureI implementing object.

=cut

sub _expand_region {
    my ($self, $feat) = @_;
    if(! $feat->isa('Bio::SeqFeatureI')) {
        $self->warn("$feat does not implement Bio::SeqFeatureI");
    }
    # if this doesn't have start/end set - forget it!
    if((! defined($self->start())) && (! defined $self->end())) {
        $self->start($feat->start());
        $self->end($feat->end());
        $self->strand($feat->strand) unless defined($self->strand());
#        $self->strand($feat->strand) unless $self->strand();
    } else {
        my $range = $self->union($feat);
        $self->start($range->start);
        $self->end($range->end);
        $self->strand($range->strand);
    }
}

1;
