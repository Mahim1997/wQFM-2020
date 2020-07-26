# $Id: Target.pm,v 1.7.4.3 2006/10/02 23:10:12 sendu Exp $
#
# BioPerl module for Bio::Annotation::Target
#
# Cared for by Scott Cain <cain@cshl.org>
#
# Copyright Scott Cain
#
# Based on the Bio::Annotation::DBLink by Ewan Birney
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::Annotation::Target - Provides an object which represents a target (ie, a  similarity hit) from one object to something in another database

=head1 SYNOPSIS

   $target1 = new Bio::Annotation::Target(-target_id  => 'F321966.1',
                                          -start      => 1,
                                          -end        => 200,
                                          -strand     => 1,   # or -1
                                         );

   # or

   $target2 = new Bio::Annotation::Target();
   $target2->target_id('Q75IM5');
   $target2->start(7);
   # ... etc ...

   # Target is-a Bio::AnnotationI object, can be added to annotation
   # collections, e.g. the one on features or seqs
   $feat->annotation->add_Annotation('Target', $target2);


=head1 DESCRIPTION

Provides an object which represents a target (ie, a similarity hit) from
one object to something in another database without prescribing what is
in the other database

=head1 AUTHOR - Scott Cain

Scott Cain - cain@cshl.org

=head1 APPENDIX

The rest of the documentation details each of the object
methods. Internal methods are usually preceded with a _

=cut


# Let the code begin...

package Bio::Annotation::Target;
use strict;
use overload '""' => sub { $_[0]->as_text || ''};
use overload 'eq' => sub { "$_[0]" eq "$_[1]" };


use base qw(Bio::Root::Root Bio::AnnotationI Bio::Range);


sub new {
  my($class,@args) = @_;

  my $self = $class->SUPER::new(@args);

  my ($target_id, $tstart, $tend, $tstrand) =
      $self->_rearrange([ qw(
                              TARGET_ID
                              START
                              END
                              STRAND ) ], @args);

  $target_id    && $self->target_id($target_id);
  $tstart       && $self->start($tstart);
  $tend         && $self->end($tend);
  $tstrand      && $self->strand($tstrand);

  return $self;
}

=head1 AnnotationI implementing functions

=cut


=head2 as_text

 Title   : as_text
 Usage   :
 Function:
 Example :
 Returns :
 Args    :


=cut

sub as_text{
  my ($self) = @_;

  my $target = $self->target_id || '';
  my $start  = $self->start     || '';
  my $end    = $self->end       || '';
  my $strand = $self->strand    || '';

   return "Target=".$target." ".$start." ".$end." ".$strand;
}

=head2 tagname

 Title   : tagname
 Usage   : $obj->tagname($newval)
 Function: Get/set the tagname for this annotation value.

           Setting this is optional. If set, it obviates the need to
           provide a tag to Bio::AnnotationCollectionI when adding
           this object. When obtaining an AnnotationI object from the
           collection, the collection will set the value to the tag
           under which it was stored unless the object has a tag
           stored already.

 Example :
 Returns : value of tagname (a scalar)
 Args    : new value (a scalar, optional)


=cut

sub tagname{
    my ($self,$value) = @_;
    if( defined $value) {
	$self->{'tagname'} = $value;
    }
    return $self->{'tagname'};
}

=head1 Specific accessors for Targets

=cut

=head2 target_id

=over

=item Usage

  $obj->target_id()        #get existing value
  $obj->target_id($newval) #set new value

=item Function

=item Returns

value of target_id (a scalar)

=item Arguments

new value of target_id (to set)

=back

=cut

sub target_id {
    my $self = shift;
    return $self->{'target_id'} = shift if defined($_[0]);
    return $self->{'target_id'};
}

1;
