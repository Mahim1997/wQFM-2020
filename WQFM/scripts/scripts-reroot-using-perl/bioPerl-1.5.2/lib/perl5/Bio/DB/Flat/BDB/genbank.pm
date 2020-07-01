#
# $Id: genbank.pm,v 1.8.4.1 2006/10/02 23:10:16 sendu Exp $
#
# BioPerl module for Bio::DB::Flat::BDB
#
# Cared for by Lincoln Stein <lstein@cshl.org>
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::DB::Flat::BDB::genbank - genbank adaptor for Open-bio standard BDB-indexed flat file

=head1 SYNOPSIS

See Bio::DB::Flat.

=head1 DESCRIPTION

This module allows genbank files to be stored in Berkeley DB flat files
using the Open-Bio standard BDB-indexed flat file scheme.  You should
not be using this directly, but instead use it via Bio::DB::Flat.

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to one
of the Bioperl mailing lists.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.  Bug reports can be submitted via the
web:

  http://bugzilla.open-bio.org/

=head1 AUTHOR - Lincoln Stein

Email - lstein@cshl.org

=head1 SEE ALSO

L<Bio::DB::Flat>,

=cut

package Bio::DB::Flat::BDB::genbank;

use strict;

use base qw(Bio::DB::Flat::BDB);

sub seq_to_ids {
  my $self = shift;
  my $seq  = shift;

  my $display_id = $seq->display_id;
  my $accession  = $seq->accession_number;
  my $version    = $seq->seq_version;
  my $gi         = $seq->primary_id;
  my %ids;
  $ids{ID}       = $display_id;
  $ids{ACC}      = $accession            if defined $accession;
  $ids{VERSION}  = "$accession.$version" if defined $accession && defined $version;
  $ids{GI}       = $gi                   if defined $gi && $gi =~ /^\d+$/;
  return \%ids;
}

sub default_primary_namespace {
  return "ID";
}

sub default_secondary_namespaces {
  return qw(ACC GI VERSION);
}

sub default_file_format { "genbank" }


1;
