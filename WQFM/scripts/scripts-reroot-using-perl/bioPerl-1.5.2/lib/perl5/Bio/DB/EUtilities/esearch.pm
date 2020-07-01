# $Id: esearch.pm,v 1.11.4.2 2006/10/02 23:10:16 sendu Exp $
#
# BioPerl module for Bio::DB::EUtilities::esearch
#
# Cared for by Chris Fields
#
# Copyright Chris Fields
#
# You may distribute this module under the same terms as perl itself
#
# POD documentation - main docs before the code
# 
# Part of the EUtilities BioPerl package

=head1 NAME

Bio::DB::EUtilities::esearch - Base interface class for handling web
queries and data retrieval from Entrez Utilities from NCBI.
You shouldn't use this class directly.

=head1 SYNOPSIS

*** Give standard usage here

=head1 DESCRIPTION

*** Describe the object here

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the
evolution of this and other Bioperl modules. Send
your comments and suggestions preferably to one
of the Bioperl mailing lists. Your participation
is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to
help us keep track the bugs and their resolution.
Bug reports can be submitted via the web.

  http://bugzilla.open-bio.org/

=head1 AUTHOR 

Email cjfields at uiuc dot edu

=head1 APPENDIX

The rest of the documentation details each of the
object methods. Internal methods are usually
preceded with a _

=cut

# Let the code begin...

package Bio::DB::EUtilities::esearch;
use strict;
use warnings;
use Bio::DB::EUtilities::Cookie;
use XML::Simple;
#use Data::Dumper;

use vars qw($EUTIL);

use base qw(Bio::DB::EUtilities);

our $EUTIL = 'esearch';

sub _initialize {
    my ($self, @args ) = @_;
    $self->SUPER::_initialize(@args);
	my ($term, $field, $reldate, $mindate, $maxdate, $datetype, $rettype, $retstart, 
        $retmax, $sort, $usehistory) = 
	  $self->_rearrange([qw(TERM FIELD RELDATE MINDATE MAXDATE DATETYPE RETTYPE
        RETSTART RETMAX SORT USEHISTORY)],
		@args);    
    # set by default
    $self->_eutil($EUTIL);
    $datetype ||= 'mdat';
    $self->datetype($datetype) if $datetype;
	$term			&& $self->term($term);
	$field			&& $self->field($field);
	$reldate		&& $self->reldate($reldate);
	$mindate		&& $self->mindate($mindate);
	$maxdate		&& $self->maxdate($maxdate);
    $retstart       && $self->retstart($retstart);
    $retmax         && $self->retmax($retmax);
    $rettype        && $self->rettype($rettype);
    $sort           && $self->sort_results($sort);
	$usehistory		&& $self->usehistory($usehistory);
}

=head2 parse_response

 Title   : parse_response
 Usage   : $db->_parse_response($content)
 Function: parse out response for cookie
 Returns : empty
 Args    : none
 Throws  : 'unparseable output exception'

=cut

sub parse_response {
    my $self    = shift;
    my $response = shift if @_;
    if (!$response || !$response->isa("HTTP::Response")) {
        $self->throw("Need HTTP::Response object");
    }
    my $history = $self->usehistory;
    my $db = $self->db;
    my $xs = XML::Simple->new();
    my $simple = $xs->XMLin($response->content);
    #$self->debug("Response dumper:\n".Dumper($simple));
    # check for major and minor errors and warnings
    if ($simple->{ERROR}) {
        $self->throw("NCBI esearch nonrecoverable error: ".$simple->{ERROR});
    }
    if ($simple->{ErrorList} || $simple->{WarningList}) {
        my %errorlist = %{ $simple->{ErrorList} };
        my %warninglist = %{ $simple->{WarningList} };
        my ($err_warn);
        for my $key (sort keys %errorlist) {
            $err_warn .= "Error : $key = $errorlist{$key}\n";
        }    
        for my $key (sort keys %warninglist) {
            $err_warn .= "Warning : $key = $warninglist{$key}\n";
        }
        chomp($err_warn);
        $self->warn("NCBI esearch Errors/Warnings:\n".$err_warn)
    }
	my $count = $simple->{Count};
	$self->esearch_count($count);
    my $id_ref = $simple->{IdList}->{Id};
    $self->_add_db_ids($id_ref) if ($id_ref);
    if ($history && $history eq 'y') {
        my $webenv = $simple->{WebEnv};
        my $querykey = $simple->{QueryKey};
		my $cookie = Bio::DB::EUtilities::Cookie->new(
										 -term 		=> $self->term,
										 -webenv    => $webenv,
										 -querykey  => $querykey,
										 -eutil     => 'esearch',
                                         -database  => $db,
										 -total		=> $count
										);
        $self->add_cookie($cookie);
	}
}

=head2 esearch_count

 Title   : esearch_count
 Usage   : $count = $db->esearch_count;
 Function: return count of number of entries retrieved by query
 Returns : integer
 Args    : none

=cut

sub esearch_count   {
    my $self = shift;
    return $self->{'_esearch_count'} = shift if @_;
    return $self->{'_esearch_count'};
}

1;
__END__