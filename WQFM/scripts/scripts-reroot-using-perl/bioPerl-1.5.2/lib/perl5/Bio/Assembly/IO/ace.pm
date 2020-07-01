# $Id: ace.pm,v 1.13.4.1 2006/10/02 23:10:12 sendu Exp $
#
## BioPerl module for Bio::Assembly::IO::ace
#
# Copyright by Robson F. de Souza
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::Assembly::IO::ace -  module to load phrap ACE files.

=head1 SYNOPSIS

    # Building an input stream
    use Bio::Assembly::IO;

    # Assembly loading methods
    $io = new Bio::Assembly::IO(-file=>"SGC0-424.fasta.screen.ace.1",
                         -format=>"ace");

    $assembly = $io->next_assembly;

=head1 DESCRIPTION

This package loads the ACE files from the (phred/phrap/consed) package
by Phill Green.  It was written to be used as a driver module for
Bio::Assembly::IO input/output.

=head2 Implemention

Assemblies are loaded into Bio::Assembly::Scaffold objects composed by
Bio::Assembly::Contig objects. In addition to default
"_aligned_coord:$seqID" feature class from Bio::Assembly::Contig, contig
objects loaded by this module will have the following special feature
classes in their feature collection:

"_align_clipping:$seqID" : location of subsequence in sequence $seqID
                           which is aligned to the contig

"_quality_clipping:$seqID" : location of good quality subsequence for
                             sequence $seqID

"consensus tags", as they are called in Consed's documentation, is
equivalent to a bioperl sequence feature and, therefore, are added to
the feature collection using their type field (see Consed's README.txt
file) as primary tag.

"read tags" are also sequence features and are stored as
sub_SeqFeatures of the sequence's coordinate feature (the
corresponding "_aligned_coord:$seqID" feature, easily accessed through
get_seq_coord() method).

"whole assembly tags" have no start and end, as they are not
associated to any particular sequence in the assembly, and are added
to the assembly's annotation collection using phrap as tag.

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to the
Bioperl mailing lists  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track
the bugs and their resolution.  Bug reports can be submitted via the web:

  http://bugzilla.open-bio.org/

=head1 AUTHOR - Robson Francisco de Souza

Email rfsouza@citri.iq.usp.br

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut

package Bio::Assembly::IO::ace;

use strict;

use Bio::Assembly::Scaffold;
use Bio::Assembly::Contig;
use Bio::Assembly::Singlet;
use Bio::LocatableSeq;
use Bio::Annotation::SimpleValue;
use Bio::Seq::Quality;
use Bio::SeqIO;
use Bio::SeqFeature::Generic;
use Dumpvalue();
my $dumper = new Dumpvalue();
$dumper->veryCompact(1);

use base qw(Bio::Assembly::IO);

=head1 Parser methods

=head2 next_assembly

 Title   : next_assembly
 Usage   : $unigene = $stream->next_assembly()
 Function: returns the next assembly in the stream
 Returns : Bio::Assembly::Scaffold object
 Args    : NONE

=cut

sub next_assembly {
    my $self = shift; # Object reference
    my $lingering_read;
    local $/="\n";

    # Resetting assembly data structure
    my $assembly = Bio::Assembly::Scaffold->new(-source=>'phrap');

    # Looping over all ACE file lines
    my ($contigOBJ,$read_name);
    my $read_data = {}; # Temporary holder for read data
    while ($_ = $self->_readline) { # By now, ACE files hold a single assembly
	chomp;

	# Loading assembly information (ASsembly field)
#	(/^AS (\d+) (\d+)/) && do {
#	    $assembly->_set_nof_contigs($1);
#	    $assembly->_set_nof_sequences_in_contigs($2);
#	};

	# Loading contig sequence (COntig sequence field)
	(/^CO Contig(\d+) (\d+) (\d+) (\d+) (\w+)/) && do { # New contig found!
	    my $contigID = $1;
	    $contigOBJ = Bio::Assembly::Contig->new(-source=>'phrap', -id=>$contigID);
#	    $contigOBJ->set_nof_bases($2); # Contig length in base pairs
#	    $contigOBJ->set_nof_reads($3); # Number of reads in this contig
#	    $contigOBJ->set_nof_segments($4); # Number of read segments selected for consensus assembly
	    my $ori = ($5 eq 'U' ? 1 : -1); # 'C' if contig was complemented (using consed) or U if not (default)
	    $contigOBJ->strand($ori);
	    my $consensus_sequence = undef;
	    while ($_ = $self->_readline) { # Looping over contig lines
		chomp;                   # Drop <ENTER> (\n) on current line
		last if (/^$/);          # Stop if empty line (contig end) is found
		s/\*/-/g; # Forcing '-' as gap symbol
		$consensus_sequence .= $_;
	    }

	    my $consensus_length = length($consensus_sequence);
	    $consensus_sequence = Bio::LocatableSeq->new(-seq=>$consensus_sequence,
							      -start=>1,
							      -end=>$consensus_length,
							      -id=>"Consensus sequence for $contigID");
	    $contigOBJ->set_consensus_sequence($consensus_sequence);
	    $assembly->add_contig($contigOBJ);
	};

	# Loading contig qualities... (Base Quality field)
	/^BQ/ && do {
	    my $consensus = $contigOBJ->get_consensus_sequence()->seq();
	    my ($i,$j,@tmp);
	    my @quality = ();
	    $j = 0;
	    while ($_ = $self->_readline) {
		chomp;
		last if (/^$/);
		@tmp = grep { /^\d+$/ } split(/\s+/);
		$i = 0;
		my $previous = 0;
		my $next     = 0;
		while ($i<=$#tmp) {
		    # IF base is a gap, quality is the average for neighbouring sites
		    if (substr($consensus,$j,1) eq '-') {
			$previous = $tmp[$i-1] unless ($i == 0);
			if ($i < $#tmp) {
			    $next = $tmp[$i+1];
			} else {
			    $next = 0;
			}
			push(@quality,int(($previous+$next)/2));
		    } else {
			push(@quality,$tmp[$i]);
			$i++;
		    }
		    $j++;
		}
	    }

	    my $qual = Bio::Seq::Quality->new(-qual=>join(" ",@quality),
                                              -id=>$contigOBJ->id());
	    $contigOBJ->set_consensus_quality($qual);
	};

	# Loading read info... (Assembled From field)
	/^AF (\S+) (C|U) (-*\d+)/ && do {
	    $read_name = $1; my $ori = $2;
	    $read_data->{$read_name}{'padded_start'} = $3; # aligned start
	    $ori = ( $ori eq 'U' ? 1 : -1);
	    $read_data->{$read_name}{'strand'}  = $ori;
	};

	# Loading base segments definitions (Base Segment field)
#	/^BS (\d+) (\d+) (\S+)/ && do {
#	    if (exists($self->{'contigs'}[$contig]{'reads'}{$3}{'segments'})) {
#		$self->{'contigs'}[$contig]{'reads'}{$3}{'segments'} .= " " . $1 . " " . $2;
#	    } else { $self->{'contigs'}[$contig]{'reads'}{$3}{'segments'} = $1 . " " . $2 }
#	};

	# Loading reads... (ReaD sequence field
	/^RD (\S+) (-*\d+) (\d+) (\d+)/ && do {
	    $read_name = $1;
	    $read_data->{$read_name}{'length'} = $2; # number_of_padded_bases
	    $read_data->{$read_name}{'contig'} = $contigOBJ;
#	    $read_data->{$read_name}{'number_of_read_info_items'} = $3;
#	    $read_data->{$read_name}{'number_of_tags'}            = $4;
	    my $read_sequence;
	    while ($_ = $self->_readline) {
		chomp;
		last if (/^$/);
		s/\*/-/g; # Forcing '-' as gap symbol
		$read_sequence .= $_; # aligned read sequence
	    }
	    my $read = Bio::LocatableSeq->new(-seq=>$read_sequence,
					      -start=>1,
					      -end=>$read_data->{$read_name}{'length'},
					      -strand=>$read_data->{$read_name}{'strand'},
					      -id=>$read_name,
					      -primary_id=>$read_name,
					      -alphabet=>'dna');
          $lingering_read = $read;
	    # Adding read location and sequence to contig ("gapped consensus" coordinates)
	    my $padded_start = $read_data->{$read_name}{'padded_start'};
	    my $padded_end   = $padded_start + $read_data->{$read_name}{'length'} - 1;
	    my $coord = Bio::SeqFeature::Generic->new(-start=>$padded_start,
						      -end=>$padded_end,
						      -strand=>$read_data->{$read_name}{'strand'},
						      -tag => { 'contig' => $contigOBJ->id }
						      );
	    $contigOBJ->set_seq_coord($coord,$read);
	};

	# Loading read trimming and alignment ranges...
	/^QA (-?\d+) (-?\d+) (-?\d+) (-?\d+)/ && do {
	    my $qual_start  = $1; my $qual_end  = $2;
	    my $align_start = $3; my $align_end = $4;

	    unless ($align_start == -1 && $align_end == -1) {
		$align_start = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$align_start);
		$align_end   = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$align_end);
		my $align_feat = Bio::SeqFeature::Generic->new(-start=>$align_start,
							       -end=>$align_end,
							       -strand=>$read_data->{$read_name}{'strand'},
							       -primary=>"_align_clipping:$read_name");
		$align_feat->attach_seq( $contigOBJ->get_seq_by_name($read_name) );
		$contigOBJ->add_features([ $align_feat ], 0);
	    }

	    unless ($qual_start == -1 && $qual_end == -1) {
		$qual_start  = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$qual_start);
		$qual_end    = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$qual_end);
		my $qual_feat = Bio::SeqFeature::Generic->new(-start=>$qual_start,
							      -end=>$qual_end,
							      -strand=>$read_data->{$read_name}{'strand'},
							      -primary=>"_quality_clipping:$read_name");
		$qual_feat->attach_seq( $contigOBJ->get_seq_by_name($read_name) );
		$contigOBJ->add_features([ $qual_feat ], 0);
	    }
	};
	     # Loading read description (DeScription fields)
          # chad was here! easter 2004.
          # lingering read is a locatableseq. is there a better way to do this?
          # i am simply adding more keys to the locatableseq
 	/^DS / && do {
 	    /CHEM: (\S+)/ && do {
 		$lingering_read->{'chemistry'} = $1;
 	    };
 	    /CHROMAT_FILE: (\S+)/ && do {
 		$lingering_read->{'chromatfilename'} = $1;
 	    };
 	    /DIRECTION: (\w+)/ && do {
 		my $ori = $1;
 		if    ($ori eq 'rev') { $ori = 'C' }
 		elsif ($ori eq 'fwd') { $ori = 'U' }
 		$lingering_read->{'strand'} = $ori;
 	    };
 	    /DYE: (\S+)/ && do {
 		$lingering_read->{'dye'} = $1;
 	    };
 	    /PHD_FILE: (\S+)/ && do {
 		$lingering_read->{'phdfilename'} = $1;
 	    };
 	    /TEMPLATE: (\S+)/ && do {
 		$lingering_read->{'template'} = $1;
 	    };
 	    /TIME: (\S+ \S+ \d+ \d+\:\d+\:\d+ \d+)/ && do {
 		$lingering_read->{'phd_time'} = $1;
 	    };
 	};

	# Loading contig tags ('tags' in phrap terminology, but Bioperl calls them features)
	/^CT\s*\{/ && do {
	    my ($contigID,$type,$source,$start,$end,$date) = split(' ',$self->_readline);
        my %tags = (source => $source, creation_date => $date);
	    $contigID =~ s/^Contig//i;
	    my $tag_type = 'extra_info';
	    while ($_ = $self->_readline) {
            if (/COMMENT\s*\{/)
            {
                $tag_type = 'comment';
            }
            elsif (/C\}/)
            {
                $tag_type = 'extra_info';
            }
            elsif (/\}/)
            {
                last;
            }
            else
            {
                $tags{$tag_type} .= "$_";
            }
	    }
	    my $contig_tag = Bio::SeqFeature::Generic->new(-start=>$start,
							   -end=>$end,
							   -primary=>$type,
							   -tag=>\%tags,
							       );
	    $assembly->get_contig_by_id($contigID)->add_features([ $contig_tag ],1);
	};

	# Loading read tags
	/^RT\s*\{/ && do {
	    my ($readID,$type,$source,$start,$end,$date) = split(' ',$self->_readline);
	    my $extra_info = undef;
	    while ($_ = $self->_readline) {
		last if (/\}/);
		$extra_info .= $_;
	    }
	    $start  = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$start);
	    $end    = $contigOBJ->change_coord("aligned $read_name",'gapped consensus',$end);
	    my $read_tag = Bio::SeqFeature::Generic->new(-start=>$start,
							 -end=>$end,
							 -primary=>$type,
							 -tag=>{ 'source' => $source,
								 'creation_date' => $date,
								 'extra_info' => $extra_info
								 });
	    my $contig = $read_data->{$readID}{'contig'};
	    my $coord  = $contig->get_seq_coord( $contig->get_seq_by_name($readID) );
	    $coord->add_sub_SeqFeature($read_tag);
	};

	# Loading read tags
	/^WA\s*\{/ && do {
	    my ($type,$source,$date) = split(' ',$self->_readline);
	    my $extra_info = undef;
	    while ($_ = $self->_readline) {
		last if (/\}/);
		$extra_info = $_;
	    }
#?	    push(@line,\@extra_info);
	    my $assembly_tag = join(" ","TYPE: ",$type,"PROGRAM:",$source,
				    "DATE:",$date,"DATA:",$extra_info);
	    $assembly_tag = Bio::Annotation::SimpleValue->new(-value=>$assembly_tag);
	    $assembly->annotation->add_Annotation('phrap',$assembly_tag);
	};

    } # while ($_ = $self->_readline)

          # hmm. what about singlets?
     my $singletsfilename = $self->file();
     $singletsfilename =~ s/\.ace.*$/.singlets/;
     $singletsfilename =~ s/\<//;
     if (!-f $singletsfilename) {
               # oh deario, no singlets here
          return $assembly;
     }
     # print("Opening the singletsfile (".$singletsfilename.")\n");
     my $singlets_fh = Bio::SeqIO->new(-file   => "<$singletsfilename",
                                          -format => 'fasta');
     my $adder;
     while (my $seq = $singlets_fh->next_seq()) {
          # $dumper->dumpValue($seq);
               # find the name of this singlet and attempt to get the phd from phd_dir instead
          my ($phdfilename,$chromatfilename) = qw(unset unset);
	  if ($seq->desc() =~ /PHD_FILE: (\S+)/) {
              $phdfilename = $1;
          }
          if ($seq->desc() =~ /CHROMAT_FILE: (\S+)/)  {
               $chromatfilename = $1;
          }
          (my $phdfile = $singletsfilename) =~ s/edit_dir.*//;
          $phdfile .= "phd_dir/$phdfilename";
          my $singlet = new Bio::Assembly::Singlet();
          if (-f $phdfile) {
               # print STDERR ("Reading singlet data from this phdfile ($phdfile)\n");
               my $phd_fh = new Bio::SeqIO( -file =>   "<$phdfile", -format     =>   'phd');
               my $swq = $phd_fh->next_seq();
               $adder = $swq;
          }
          else {
               $adder = $seq;
          }
          $adder->{phdfilename} = $phdfilename;
          $adder->{chromatfilename} = $chromatfilename;
          $singlet->seq_to_singlet($adder);
          $assembly->add_singlet($singlet);
     }
    $assembly->update_seq_list();
    return $assembly;
}

=head2 write_assembly

    Title   : write_assembly
    Usage   : $ass_io->write_assembly($assembly)
    Function: Write the assembly object in Phrap compatible ACE format
    Returns : 1 on success, 0 for error
    Args    : A Bio::Assembly::Scaffold object

=cut

sub write_assembly {
    my $self = shift;
    $self->throw_not_implemented();
}


1;

__END__
