# $Id: qual.pm,v 1.30.4.1 2006/10/02 23:10:30 sendu Exp $
#
# Copyright (c) 1997-9 bioperl, Chad Matsalla. All Rights Reserved.
#           This module is free software; you can redistribute it and/or
#           modify it under the same terms as Perl itself.
#
# Copyright Chad Matsalla
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::SeqIO::qual - .qual file input/output stream

=head1 SYNOPSIS

Do not use this module directly.  Use it via the Bio::SeqIO class
(see L<Bio::SeqIO> for details).

  my $in_qual = Bio::SeqIO->new(-file    => $qualfile,
                                -format  => 'qual',
                                -verbose => $verbose);

=head1 DESCRIPTION

This object can transform .qual (similar to fasta) objects to and from
Bio::Seq::Quality objects. See L<Bio::Seq::Quality> for details.

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

=head1 AUTHOR Chad Matsalla

Chad Matsalla
bioinformatics@dieselwurks.com

=head1 CONTRIBUTORS

Jason Stajich, jason@bioperl.org

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut

# Let the code begin...

package Bio::SeqIO::qual;
use strict;
use Bio::Seq::SeqFactory;
use Dumpvalue();
my $dumper = new Dumpvalue();

use base qw(Bio::SeqIO);

sub _initialize {
	my($self,@args) = @_;
	$self->SUPER::_initialize(@args);
	if( ! defined $self->sequence_factory ) {
		$self->sequence_factory(new Bio::Seq::SeqFactory
										(-verbose => $self->verbose(),
										 -type => 'Bio::Seq::PrimaryQual'));
	}
}

=head2 next_seq()

 Title   : next_seq()
 Usage   : $scf = $stream->next_seq()
 Function: returns the next scf sequence in the stream
 Returns : Bio::Seq::PrimaryQual object
 Notes   : Get the next quality sequence from the stream.

=cut

sub next_seq {
    my ($self,@args) = @_;
    my ($qual,$seq);
    my $alphabet;
    local $/ = "\n>";

    return unless my $entry = $self->_readline;

    if ($entry eq '>')  {	# very first one
		 return unless $entry = $self->_readline;
    }

    # original: my ($top,$sequence) = $entry =~ /^(.+?)\n([^>]*)/s
    my ($top,$sequence) = $entry =~ /^(.+?)\n([^>]*)/s
		or $self->throw("Can't parse entry [$entry]");
    my ($id,$fulldesc) = $top =~ /^\s*(\S+)\s*(.*)/
		or $self->throw("Can't parse fasta header");
    $id =~ s/^>//;
    # create the seq object
    $sequence =~ s/\n+/ /g;
    return $self->sequence_factory->create
		(-qual        => $sequence,
		 -id         => $id,
		 -primary_id => $id,
		 -display_id => $id,
		 -desc       => $fulldesc
		);
}

=head2 _next_qual

 Title   : _next_qual
 Usage   : $seq = $stream->_next_qual() (but do not do
      	  that. Use $stream->next_seq() instead)
 Function: returns the next quality in the stream
 Returns : Bio::Seq::PrimaryQual object
 Args    : NONE
 Notes	: An internal method. Gets the next quality in
	        the stream.

=cut

sub _next_qual {
	my $qual = next_primary_qual( $_[0], 1 );
	return $qual;
}

=head2 next_primary_qual()

 Title   : next_primary_qual()
 Usage   : $seq = $stream->next_primary_qual()
 Function: returns the next sequence in the stream
 Returns : Bio::PrimaryQual object
 Args    : NONE

=cut

sub next_primary_qual {
	# print("CSM next_primary_qual!\n");
	my( $self, $as_next_qual ) = @_;
	my ($qual,$seq);
	local $/ = "\n>";

	return unless my $entry = $self->_readline;

	if ($entry eq '>')  {  # very first one
		return unless $entry = $self->_readline;
	}

	my ($top,$sequence) = $entry =~ /^(.+?)\n([^>]*)/s
      or $self->throw("Can't parse entry [$entry]");
	my ($id,$fulldesc) = $top =~ /^\s*(\S+)\s*(.*)/
      or $self->throw("Can't parse fasta header");
	$id =~ s/^>//;
	# create the seq object
	$sequence =~ s/\n+/ /g;
	if ($as_next_qual) {
            $qual = Bio::Seq::PrimaryQual->new(-qual       => $sequence,
                                               -id         => $id,
                                               -primary_id => $id,
                                               -display_id => $id,
                                               -desc       => $fulldesc
                                              );
	}
	return $qual;
}

=head2 write_seq

 Title   : write_seq
 Usage   : $obj->write_seq( -source => $source,
		            -header => "some information");
 Function: Write out a list of quality values to a fasta-style file.
 Returns : Nothing.
 Args    : Requires a reference to a Bio::Seq::Quality object or a
	        PrimaryQual object as the -source. Optional: information
	        for the header.
 Notes   : If no -header is provided, $obj->id() will be used where
	        $obj is a reference to either a Quality object or a
	        PrimaryQual object. If $source->id() fails, "unknown" will be
	        the header. If the Quality object has $source->length()
           of "DIFFERENT" (read the pod, luke), write_seq will use the
           length of the PrimaryQual object within the Quality
           object.

=cut

sub write_seq {
	my ($self,@args) = @_;
	my ($source)  = $self->_rearrange([qw(SOURCE HEADER)], @args);
	if (!$source || ( !$source->isa('Bio::Seq::Quality') &&
							!$source->isa('Bio::Seq::PrimaryQual')   )) {
		$self->throw("You must pass a Bio::Seq::Quality or a Bio::Seq::PrimaryQual object to write_seq() as a parameter named \"source\"");
	}
	my $header = ($source->can("header") && $source->header) ?
	              $source->header :
	             ($source->can("id") && $source->id) ?
		           $source->id :
					  "unknown";
	my @quals = $source->qual();
	# ::dumpValue(\@quals);
	$self->_print (">$header \n");
	my (@slice,$max,$length);
	$length = $source->length();
#	if ($length eq "DIFFERENT") {
#		$self->warn("You passed a Bio::Seq::Quality object that contains a sequence and quality of differing lengths. Using the length of the PrimaryQual component of the Quality object.");
#		$length = $source->qual_obj()->length();
#    }
	# print("Printing $header to a file.\n");
	for (my $count = 1; $count<=$length; $count+= 50) {
		if ($count+50 > $length) { $max = $length; }
		else { $max = $count+49; }
		my @slice = @{$source->subqual($count,$max)};
		$self->_print (join(' ',@slice), "\n");
	}

	$self->flush if $self->_flush_on_write && defined $self->_fh;
	return 1;
}


1;

__END__
