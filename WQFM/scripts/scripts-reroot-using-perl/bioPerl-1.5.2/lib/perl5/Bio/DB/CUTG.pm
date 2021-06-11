# $Id: CUTG.pm,v 1.11.4.2 2006/10/02 23:10:14 sendu Exp $
#
# BioPerl module for Bio::DB::CUTG
#
# Cared for by Richard Adams (richard.adams@ed.ac.uk)
#
# Copyright Richard Adams
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::DB::CUTG - for access to the Codon usage Database
at http://www.kazusa.or.jp/codon.

=head1 SYNOPSIS

       use Bio::CodonUsage::Table; 
       use Bio::DB::CUTG;

       my $db = Bio::DB::CUTG->new(-sp =>'Pan troglodytes');
       my $CUT = $db->get_request();


=head1 DESCRIPTION

This class retrieves and objectifies codon usage tables either from the
CUTG web database . The idea is that you can initially retrieve a CUT from
the web database, and write it to file in a way that can be read in
later, using the Bio::CodonUsage::IO module.

For a web query, two parameters need to be specified: species(sp) and
genetic code id (gc). The database is searched using regular
expressions, therefore the full latin name must be given to specify
the organism. If the species name is ambiguous the first CUT in the
list is retrieved.  Defaults are Homo sapiens and 1(standard genetic
code).  If you are retrieving CUTs from organisms using other genetic
codes this needs to be put in as a parameter. Parameters can be
entered in the constructor or in the get_web_request
()method. Allowable parameters are listed in the $QUERY_KEYS hash
reference variable.

I intend at a later date to allow retrieval of multiple codon tables
e.g., from a wildcard search.

=head1 SEE ALSO

L<Bio::Tools::CodonTable>, 
L<Bio::WebAgent>, 
L<Bio::CodonUsage::Table>, 
L<Bio::CodonUsage::IO>

=head1 FEEDBACK

=head2 Mailing Lists


User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to one
of the Bioperl mailing lists.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.  Bug reports can be submitted via the web:

  http://bugzilla.open-bio.org/

=head1 AUTHORS

Richard Adams, Richard.Adams@ed.ac.uk

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut


# Let the code begin...



package Bio::DB::CUTG;
use Bio::CodonUsage::IO;
use IO::String;
use vars qw($URL $QUERY_KEYS);

use base qw(Bio::WebAgent);

$QUERY_KEYS = { 
				sp => 'full Latin species name',	
				gc => 'genetic code id'
			 };

BEGIN {
		 $URL = "http://www.kazusa.or.jp"
	}


=head2 new

 Title   : new
 Usage   : my $db = Bio::DB::CUTG->new()
 Returns : a reference to a new Bio::DB::CUTG 
 Args    : hash of optional values for db query

=cut

sub new {
	my ($class, @args ) =@_;
	_check_args(@args);
	my $self = $class->SUPER::new(@args);
	return $self;
}

=head2 query_keys

 Title   : query_keys
 Usage   : $db->query_keys()
 Purpose : To determine valid keys for parameters for db query.
 Returns : a reference to a hash describing valid query keys
 Args    : none

=cut

sub query_keys {
	return $QUERY_KEYS;
	}

=head2  sp

 Title  : sp
 Usage  : my $sp = $db->sp();
 Purpose: Get/set method for species name
 Returns: void or species name string
 Args   : None or species name string

=cut

sub sp {
	my $self = shift;
	if (@_) {
		my $name = shift;
		if ($name =~ /[^\w\s]/) {
			$self->warn (" contains non-word characters, setting to default
							of Homo sapiens");
			$self->{'_sp'} = "Homo sapiens";
				}
		else{
			$self->{'_sp'} = $name;
			}
		}
	return $self->{'_sp'}|| "Homo sapiens";
	
}

=head2  gc

 Title  : gc
 Usage  : my $gc = $db->gc();
 Purpose: Get/set method for genetic code id
 Returns: void or genetic code  integer
 Args   : None or genetic code integer

=cut

sub gc {
	#### genetic code id for translations ####
	my $self = shift;
	if (@_) {
		if($_[0] =~ /^\d+$/ && $_[0] >= 1 && $_[0] <=15 && $_[0] != 7 
				&& $_[0] != 8) {
			$self->{'_gc'} = shift;
			}
		else {
			$self->warn("invalid genetic code index - setting to standard default (1)");
			$self->{'_gc'} = 1;
			}
		}
	return $self->{'_gc'} || 1; #return 1 if not defined

	}


=head2  get_request

 Title  : get_web_request
 Usage  : my $cut = $db->get_web_request();
 Purpose: To query remote CUT with a species name
 Returns: a new codon usage table object 
 Args   : species  name(mandatory), genetic code id(optional)

=cut

sub get_request {
	my ($self, @args) = @_;
	_check_args(@args);
	shift;
	### can put in parameters here as well
	while( @_ ) {
	my $key = shift;
        $key =~ s/^-//;
        $self->$key(shift);
    }	
	$self->url($URL);

	###1st of all search DB to check species exists and is unique
	my $nameparts =  join "+", $self->sp =~ /(\w+)/g;
	my $search_url = $self->url . "/codon/cgi-bin/spsearch.cgi?species=" 
					. $nameparts . "&c=s";
	my $rq = HTTP::Request->new(GET=>$search_url);
	my $reply = $self->request($rq);
    if ($reply->is_error) {
        $self->throw($reply->as_string()."\nError getting for url $search_url!\n");
    }
	my $content = $reply->content;
        return 0 unless $content;
    $self->debug (" reply from query is \n  $content");
	#####  if no matches, assign defaults - or can throw here?  ######
	if ($content =~ /not found/i) {
		$self->warn ("organism not found -selecting human as default");
		$self->sp("Homo sapiens");
		$self->_db("gbpri");
	
	}

	
	else {
		my @names = $content =~ /(species)/g;
		### get 1st species data from report ####
		my ($sp, $db)  = $content =~ /species=(.*)\+\[(\w+)\]"/;
		
		$sp =~ s/\+/ /g;
		## warn if  more than 1 matching species ##
		## if multiple species retrieved, choose first one by default ##
		if (@names >1 ){
			$self->warn ("too many species - not a unique species id - selecting $sp  ");
			}
		### now assign species and database value
		$self->sp($sp);
		$self->_db($db);
		}


	######## now get codon table , all defaults established now

	##construct URL##
	$nameparts =  join "+", $self->sp =~ /(\w+)/g;
	my $CT_url = $self->url . "/codon/cgi-bin/showcodon.cgi?species="
				. $nameparts . "+%5B" . $self->_db . "%5D&aa=" . $self->gc . "&style=GCG";

	## retrieve data in html##
	my $rq2 = HTTP::Request->new(GET=>$CT_url);
    $reply = $self->request($rq2);
    if ($reply->is_error) {
        $self->throw($reply->as_string()."\nError getting for url $CT_url!\n");
    }
	my $content2 = $reply->content;

	## strip html tags, basic but works here
	$content2 =~ s/<[^>]+>//sg;
	$content2 =~ s/Format.*//sg;
    $self->debug ("raw DDB table is :\n $content2");

	### and pass to Bio::CodonUsage::IO for parsing
	my $iostr = IO::String->new($content2);
	my $io = Bio::CodonUsage::IO->new (-fh=>$iostr);

	##return object ##
	return $io->next_data;
	}



sub _check_args {

	###checks parameters for matching $QUERYKEYS
	my @args = @_;
	while (my $key = lc(shift @args)) {
		$key =~ s/\-//;
		
		if (!exists ($QUERY_KEYS->{$key})) {
			Bio::Root::Root->throw("invalid parameter - must be one of [" .
						(join "] [", keys %$QUERY_KEYS) . "]");
		}
		shift @args;
	}
}

#### internal URL parameter not specifiable ######
sub _db {
	my $self = shift;
	if (@_) {
		$self->{'_db'} = shift;
		}
	return $self->{'_db'};
}

1;
