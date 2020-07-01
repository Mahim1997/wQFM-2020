# $Id: EPCR.pm,v 1.18.4.1 2006/10/02 23:10:32 sendu Exp $
#
# BioPerl module for Bio::Tools::EPCR
#
# Cared for by Jason Stajich <jason-at-bioperl.org>
#
# Copyright Jason Stajich
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::Tools::EPCR - Parse ePCR output and make features

=head1 SYNOPSIS

    # A simple annotation pipeline wrapper for ePCR data
    # assuming ePCR data is already generated in file seq1.epcr
    # and sequence data is in fasta format in file called seq1.fa

    use Bio::Tools::EPCR;
    use Bio::SeqIO;
    my $parser = new Bio::Tools::EPCR(-file => 'seq1.epcr');
    my $seqio = new Bio::SeqIO(-format => 'fasta', -file => 'seq1.fa');
    my $seq = $seqio->next_seq || die("cannot get a seq object from SeqIO");

    while( my $feat = $parser->next_feature ) {
	# add EPCR annotation to a sequence
	$seq->add_SeqFeature($feat);
    }
    my $seqout = new Bio::SeqIO(-format => 'embl');
    $seqout->write_seq($seq);


=head1 DESCRIPTION

This object serves as a parser for ePCR data, creating a
Bio::SeqFeatureI for each ePCR hit.  These can be processed or added
as annotation to an existing Bio::SeqI object for the purposes of
automated annotation.

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to
the Bioperl mailing list.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
of the bugs and their resolution. Bug reports can be submitted via the
web:

  http://bugzilla.open-bio.org/

=head1 AUTHOR - Jason Stajich

Email jason-at-bioperl.org

=head1 APPENDIX

The rest of the documentation details each of the object methods.
Internal methods are usually preceded with a _

=cut


# Let the code begin...


package Bio::Tools::EPCR;
use strict;

use Bio::SeqFeature::FeaturePair;
use Bio::SeqFeature::Generic;

use base qw(Bio::Root::Root Bio::SeqAnalysisParserI Bio::Root::IO);

=head2 new

 Title   : new
 Usage   : my $epcr = new Bio::Tools::EPCR(-file => $file,
					   -primary => $fprimary, 
					   -source => $fsource, 
					   -groupclass => $fgroupclass);
 Function: Initializes a new EPCR parser
 Returns : Bio::Tools::EPCR
 Args    : -fh   => filehandle
           OR
           -file => filename

           -primary => a string to be used as the common value for
                       each features '-primary' tag.  Defaults to
                       'sts'.  (This in turn maps to the GFF 'type'
                       tag (aka 'method')).

            -source => a string to be used as the common value for
                       each features '-source' tag.  Defaults to
                       'e-PCR'. (This in turn maps to the GFF 'source'
                       tag)

             -groupclass => a string to be used as the name of the tag
                           which will hold the sts marker namefirst
                           attribute.  Defaults to 'name'.

=cut

sub new {
  my($class,@args) = @_;

  my $self = $class->SUPER::new(@args);
  my ($primary, $source, 
      $groupclass) = $self->_rearrange([qw(PRIMARY
					   SOURCE 
					   GROUPCLASS)],@args);
  $self->primary(defined $primary ? $primary : 'sts');
  $self->source(defined $source ? $source : 'e-PCR');
  $self->groupclass(defined $groupclass ? $groupclass : 'name');

  $self->_initialize_io(@args);
  return $self;
}

=head2 next_feature

 Title   : next_feature
 Usage   : $seqfeature = $obj->next_feature();
 Function: Returns the next feature available in the analysis result, or
           undef if there are no more features.
 Example :
 Returns : A Bio::SeqFeatureI implementing object, or undef if there are no
           more features.
 Args    : none    

=cut

sub next_feature {
    my ($self) = @_;
    my $line = $self->_readline;
    return unless defined($line);
    chomp($line);
    my($seqname,$location,$mkrname, $rest) = split(/\s+/,$line,4);
    
    my ($start,$end) = ($location =~ /(\S+)\.\.(\S+)/);

    # `e-PCR -direct` results code match strand in $rest as (+) and (-).  Decode it if present.
    my $strandsign;
    if ($rest =~ m/^\(([+-])\)(.*)$/) {
      ($strandsign,$rest) = ($1, $2);
    } else {
      $strandsign = "?";
    }
    my $strand = $strandsign eq "+" ? 1 :  $strandsign eq "-" ? -1 : 0;

    my $markerfeature = new Bio::SeqFeature::Generic 
	( '-start'   => $start,
	  '-end'     => $end,
	  '-strand'  => $strand,
	  '-source'  => $self->source,
	  '-primary' => $self->primary,
	  '-seq_id'  => $seqname,
	  '-tag'     => {
	      $self->groupclass => $mkrname,
	      ($rest ? ('Note'            => $rest ) : ()),
	  });
    #$markerfeature->add_tag_value('Note', $rest) if defined $rest;
    return $markerfeature;
}

=head2 source

 Title   : source
 Usage   : $obj->source($newval)
 Function: 
 Example : 
 Returns : value of source (a scalar)
 Args    : on set, new value (a scalar or undef, optional)


=cut

sub source{
    my $self = shift;
    return $self->{'_source'} = shift if @_;
    return $self->{'_source'};
}

=head2 primary

 Title   : primary
 Usage   : $obj->primary($newval)
 Function: 
 Example : 
 Returns : value of primary (a scalar)
 Args    : on set, new value (a scalar or undef, optional)


=cut

sub primary{
    my $self = shift;
    return $self->{'_primary'} = shift if @_;
    return $self->{'_primary'};
}

=head2 groupclass

 Title   : groupclass
 Usage   : $obj->groupclass($newval)
 Function: 
 Example : 
 Returns : value of groupclass (a scalar)
 Args    : on set, new value (a scalar or undef, optional)


=cut

sub groupclass{
    my $self = shift;

    return $self->{'_groupclass'} = shift if @_;
    return $self->{'_groupclass'};
}

1;
