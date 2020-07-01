# $Id: egquery.pm,v 1.6.4.1 2006/10/02 23:10:15 sendu Exp $
#
# BioPerl module for Bio::DB::EUtilities::egquery
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

Bio::DB::EUtilities::egquery - counts for a global query of Entrez databases

=head1 SYNOPSIS

    my $egquery = Bio::DB::EUtilities->new(
                                     -eutil    => 'egquery',
                                     -term     => 'dihydroorotase'
                                      );

    print $egquery->get_response->content;

=head1 DESCRIPTION

L<EGQuery|Bio::DB::EUtilities::egquery> provides Entrez database counts
in XML for a single search using NCBI's Global Query.  No further parsing of
the XML data is processed at this time.

=head2 NCBI EGQuery Parameters

The following are a general list of parameters that can be used to take
advantage of EGQuery.  Up-to-date help for EGQuery is available at this URL
(the information below is a summary of the options found there):

  http://eutils.ncbi.nlm.nih.gov/entrez/query/static/egquery_help.html

=over 3

=item C<term>

Search term or phrase with or without Boolean operators.  This can use search
field descriptions and tags (Note: these may be database specific and are
better used with L<ESearch|Bio::DB::EUtilities::esearch>.

=back

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the
evolution of this and other Bioperl modules. Send
your comments and suggestions preferably to one
of the Bioperl mailing lists. Your participation
is much appreciated.

  bioperl-l@lists.open-bio.org               - General discussion
  http://www.bioperl.org/wiki/Mailing_lists  - About the mailing lists

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

package Bio::DB::EUtilities::egquery;
use strict;
use warnings;

use vars qw($EUTIL);

use base qw(Bio::DB::EUtilities);

our $EUTIL = 'egquery';

sub _initialize {
    my ($self, @args ) = @_;
    $self->SUPER::_initialize(@args);
	my ($term) =  $self->_rearrange([qw(TERM)],@args);	
    # set by default
    $self->_eutil($EUTIL);
    $term	        && $self->term($term);
}

=head2 parse_response

 Title   : parse_response
 Usage   : $db->_parse_response($content)
 Function: parse out response for cookie
 Returns : empty
 Args    : none
 Throws  : 'unparseable output exception'

=cut

# EGQuery doesn't have error checking, so this is NOOP for now

sub parse_response {
}

1;
__END__