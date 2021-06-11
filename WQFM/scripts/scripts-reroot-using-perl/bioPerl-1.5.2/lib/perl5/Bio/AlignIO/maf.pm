# $Id: maf.pm,v 1.10.4.1 2006/10/02 23:10:12 sendu Exp $
#
# BioPerl module for Bio::AlignIO::maf
#
# Copyright Allen Day
#

=head1 NAME

Bio::AlignIO::maf - Multipla Alignment Format sequence input stream

=head1 SYNOPSIS

 Do not use this module directly.  Use it via the Bio::AlignIO class.

 use Bio::AlignIO;

 my $alignio = Bio::AlignIO->new(-fh => \*STDIN, -format => 'maf');

 while(my $aln = $alignio->next_aln()){
   my $match_line = $aln->match_line;

   print $aln, "\n";

   print $aln->length, "\n";
   print $aln->no_residues, "\n";
   print $aln->is_flush, "\n";
   print $aln->no_sequences, "\n";

   $aln->splice_by_seq_pos(1);

   print $aln->consensus_string(60), "\n";
   print $aln->get_seq_by_pos(1)->seq, "\n";
   print $aln->match_line(), "\n";

   print "\n";
 }

=head1 DESCRIPTION

This class constructs Bio::SimpleAlign objects from an MAF-format
multiple alignment file.

Writing in MAF format is currently unimplemented.

Spec of MAF format is here:
  http://hgwdev-sugnet.cse.ucsc.edu/cgi-bin/hgGateway?org=human

=head1 FEEDBACK

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.  Bug reports can be submitted via the
web:

  http://bugzilla.open-bio.org/

=head1 AUTHORS - Allen Day

Email: allenday@ucla.edu

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut

# Let the code begin...

package Bio::AlignIO::maf;
use vars qw($seen_header);
use strict;

use Bio::SimpleAlign;

$seen_header = 0;

use base qw(Bio::AlignIO);

=head2 new

 Title   : new
 Usage   : my $alignio = new Bio::AlignIO(-format => 'maf'
					  -file   => '>file',
					  -idlength => 10,
					  -idlinebreak => 1);
 Function: Initialize a new L<Bio::AlignIO::maf> reader
 Returns : L<Bio::AlignIO> object
 Args    :

=cut

sub _initialize {
  my($self,@args) = @_;
  $self->SUPER::_initialize(@args);

  1;
}

=head2 next_aln

 Title   : next_aln
 Usage   : $aln = $stream->next_aln()
 Function: returns the next alignment in the stream.
           Throws an exception if trying to read in PHYLIP
           sequential format.
 Returns : L<Bio::SimpleAlign> object
 Args    : 

=cut

sub next_aln {
    my $self = shift;

    if(!$seen_header){
	my $line = $self->_readline;
	$self->throw("This doesn't look like a MAF file.  First line should start with ##maf, but it was: ".$line)
	    unless $line =~ /^##maf/;
	$seen_header = 1;
    }

    my $aln =  Bio::SimpleAlign->new(-source => 'maf');

    my($aline, @slines);
    while(my $line = $self->_readline()){
	$aline = $line if $line =~ /^a/;
	push @slines, $line if $line =~ /^s /;
	last if $line !~ /\S/;

    }

    return unless $aline;

    my($kvs) = $aline =~ /^a\s+(.+)$/;
    my @kvs  = split /\s+/, $kvs if $kvs;
    my %kv;
    foreach my $kv (@kvs){
	my($k,$v) = $kv =~ /(.+)=(.+)/;
	$kv{$k} = $v;
    }

    $aln->score($kv{score});

    foreach my $sline (@slines){
	my($s,$src,$start,$size,$strand,$srcsize,$text) =
	    split /\s+/, $sline;
	# adjust coordinates to be one-based inclusive
        $start = $start + 1;
	my $seq = new Bio::LocatableSeq('-seq'    => $text,
					'-id'     => $src,
					'-start'  => $start,
					'-end'    => $start + $size - 1,
					'-strand' => $strand,
					);
	$aln->add_seq($seq);
    }

    return $aln;
}

sub write_aln {
  shift->throw_not_implemented
}

1;
