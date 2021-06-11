# $Id: alf.pm,v 1.12.4.1 2006/10/02 23:10:28 sendu Exp $
# BioPerl module for Bio::SeqIO::alf
#
# Cared for by Aaron Mackey <amackey@virginia.edu>
#
# Copyright Aaron Mackey
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::SeqIO::alf - alf trace sequence input/output stream

=head1 SYNOPSIS

Do not use this module directly.  Use it via the Bio::SeqIO class.

=head1 DESCRIPTION

This object can transform Bio::Seq objects to and from alf trace
files.

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to one
of the Bioperl mailing lists.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.
Bug reports can be submitted via the web:

  http://bugzilla.open-bio.org/

=head1 AUTHORS - Aaron Mackey

Email: amackey@virginia.edu

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut

# Let the code begin...

package Bio::SeqIO::alf;
use vars qw(@ISA $READ_AVAIL);
use strict;

use Bio::SeqIO;
use Bio::Seq::SeqFactory;

push @ISA, qw( Bio::SeqIO );

sub BEGIN {
    eval { require Bio::SeqIO::staden::read; };
    if ($@) {
	$READ_AVAIL = 0;
    } else {
	push @ISA, "Bio::SeqIO::staden::read";
	$READ_AVAIL = 1;
    }
}

sub _initialize {
  my($self,@args) = @_;
  $self->SUPER::_initialize(@args);  
  if( ! defined $self->sequence_factory ) {
      $self->sequence_factory(new Bio::Seq::SeqFactory(-verbose => $self->verbose(), -type => 'Bio::Seq'));      
  }
  unless ($READ_AVAIL) {
      Bio::Root::Root->throw( -class => 'Bio::Root::SystemException',
			      -text  => "Bio::SeqIO::staden::read is not available; make sure the bioperl-ext package has been installed successfully!"
			    );
  }
}

=head2 next_seq

 Title   : next_seq
 Usage   : $seq = $stream->next_seq()
 Function: returns the next sequence in the stream
 Returns : Bio::SeqWithQuality object
 Args    : NONE

=cut

sub next_seq {

    my ($self) = @_;

    my ($seq, $id, $desc, $qual) = $self->read_trace($self->_fh, 'alf');

    # create the seq object
    $seq = $self->sequence_factory->create(-seq        => $seq,
					   -id         => $id,
					   -primary_id => $id,
					   -desc       => $desc,
					   -alphabet   => 'DNA',
					   -qual       => $qual
					   );
    return $seq;
}

=head2 write_seq

 Title   : write_seq
 Usage   : $stream->write_seq(@seq)
 Function: writes the $seq object into the stream
 Returns : 1 for success and 0 for error
 Args    : Bio::Seq object


=cut

sub write_seq {
    my ($self,@seq) = @_;

    my $fh = $self->_fh;
    foreach my $seq (@seq) {
	$self->write_trace($fh, $seq, 'alf');
    }

    $self->flush if $self->_flush_on_write && defined $self->_fh;
    return 1;
}

1;
