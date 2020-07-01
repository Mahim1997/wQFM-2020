# $Id: WrapperBase.pm,v 1.21.4.2 2006/10/17 09:12:57 sendu Exp $
#
# BioPerl module for Bio::Tools::Run::WrapperBase
#
# Cared for by Jason Stajich <jason@bioperl.org>
#
# Copyright Jason Stajich
#
# You may distribute this module under the same terms as perl itself

# POD documentation - main docs before the code

=head1 NAME

Bio::Tools::Run::WrapperBase - A Base object for wrappers around executables

=head1 SYNOPSIS

  # do not use this object directly, it provides the following methods
  # for its subclasses

  my $errstr = $obj->error_string();
  my $exe    = $obj->executable();
  $obj->save_tempfiles($booleanflag)
  my $outfile= $obj->outfile_name();
  my $tempdir= $obj->tempdir(); # get a temporary dir for executing
  my $io     = $obj->io;  # Bio::Root::IO object
  my $cleanup= $obj->cleanup(); # remove tempfiles

  $obj->run({-arg1 => $value});

=head1 DESCRIPTION

This is a basic module from which to build executable wrapper modules.
It has some basic methods to help when implementing new modules.

=head1 FEEDBACK

=head2 Mailing Lists

User feedback is an integral part of the evolution of this and other
Bioperl modules. Send your comments and suggestions preferably to
the Bioperl mailing list.  Your participation is much appreciated.

  bioperl-l@bioperl.org                  - General discussion
  http://bioperl.org/wiki/Mailing_lists  - About the mailing lists

=head2 Reporting Bugs

Report bugs to the Bioperl bug tracking system to help us keep track of
the bugs and their resolution. Bug reports can be submitted via the
web:

  http://bugzilla.open-bio.org/

=head1 AUTHOR - Jason Stajich

Email jason-at-bioperl.org

=head1 APPENDIX

The rest of the documentation details each of the object methods.
Internal methods are usually preceded with a _

=cut


# Let the code begin...


package Bio::Tools::Run::WrapperBase;
use strict;

# Object preamble - inherits from Bio::Root::Root

use base qw(Bio::Root::Root);

use File::Spec;
use File::Path qw(); # don't import anything

=head2 run

 Title   : run
 Usage   : $wrapper->run({ARGS HERE});
 Function: Support generic running with args passed in
           as a hashref
 Returns : Depends on the implementation, status OR data
 Args    : hashref of named arguments


=cut

sub run {
   my ($self,@args) = @_;
   $self->throw_not_implemented();
}


=head2 error_string

 Title   : error_string
 Usage   : $obj->error_string($newval)
 Function: Where the output from the last analysis run is stored.
 Returns : value of error_string
 Args    : newvalue (optional)


=cut

sub error_string{
   my ($self,$value) = @_;
   if( defined $value) {
      $self->{'_error_string'} = $value;
    }
    return $self->{'_error_string'} || '';
}

=head2 arguments

 Title   : arguments
 Usage   : $obj->arguments($newval)
 Function: Commandline parameters 
 Returns : value of arguments
 Args    : newvalue (optional)


=cut

sub arguments {
  my ($self,$value) = @_;
  if(defined $value) {
    $self->{'_arguments'} = $value;
  }
  return $self->{'_arguments'} || '';
}


=head2 no_param_checks

 Title   : no_param_checks
 Usage   : $obj->no_param_checks($newval)
 Function: Boolean flag as to whether or not we should
           trust the sanity checks for parameter values
 Returns : value of no_param_checks
 Args    : newvalue (optional)


=cut

sub no_param_checks{
   my ($self,$value) = @_;
   if( defined $value || ! defined $self->{'no_param_checks'} ) {
       $value = 0 unless defined $value;
      $self->{'no_param_checks'} = $value;
    }
    return $self->{'no_param_checks'};
}

=head2 save_tempfiles

 Title   : save_tempfiles
 Usage   : $obj->save_tempfiles($newval)
 Function: 
 Returns : value of save_tempfiles
 Args    : newvalue (optional)


=cut

sub save_tempfiles{
   my ($self,$value) = @_;
   if( defined $value) {
      $self->{'save_tempfiles'} = $value;
    }
    return $self->{'save_tempfiles'};
}

=head2 outfile_name

 Title   : outfile_name
 Usage   : my $outfile = $wrapper->outfile_name();
 Function: Get/Set the name of the output file for this run
           (if you wanted to do something special)
 Returns : string
 Args    : [optional] string to set value to


=cut

sub outfile_name{
   my ($self,$nm) = @_;
   if( defined $nm || ! defined $self->{'_outfilename'} ) { 
       $nm = 'mlc' unless defined $nm;
       $self->{'_outfilename'} = $nm;
   }
   return $self->{'_outfilename'};
}


=head2 tempdir

 Title   : tempdir
 Usage   : my $tmpdir = $self->tempdir();
 Function: Retrieve a temporary directory name (which is created)
 Returns : string which is the name of the temporary directory
 Args    : none


=cut

sub tempdir{
   my ($self) = shift;

   $self->{'_tmpdir'} = shift if @_;
   unless( $self->{'_tmpdir'} ) {
       $self->{'_tmpdir'} = $self->io->tempdir(CLEANUP => ! $self->save_tempfiles );
   }
   unless( -d $self->{'_tmpdir'} ) {
       mkdir($self->{'_tmpdir'},0777);
   }
   return $self->{'_tmpdir'};
}

=head2 cleanup

 Title   : cleanup
 Usage   : $wrapper->cleanup();
 Function: Will cleanup the tempdir directory
 Returns : none
 Args    : none


=cut

sub cleanup{
   my ($self) = @_;
   $self->io->_io_cleanup();
   if( defined $self->{'_tmpdir'} && -d $self->{'_tmpdir'} ) {
       # $self->io->rmtree($self->{'_tmpdir'});
       File::Path->rmtree( $self->{'_tmpdir'} );
   }
}

=head2 io

 Title   : io
 Usage   : $obj->io($newval)
 Function: Gets a Bio::Root::IO object
 Returns : Bio::Root::IO object
 Args    : none


=cut

sub io{
   my ($self) = @_;
   unless( defined $self->{'io'} ) {
       $self->{'io'} = new Bio::Root::IO(-verbose => $self->verbose());
   }
    return $self->{'io'};
}

=head2 version

 Title   : version
 Usage   : $version = $wrapper->version()
 Function: Returns the program version (if available)
 Returns : string representing version of the program 
 Args    : [Optional] value to (re)set version string


=cut

sub version{
   my ($self,@args) = @_;
   return;
}

=head2 executable

 Title   : executable
 Usage   : my $exe = $factory->executable();
 Function: Finds the full path to the executable
 Returns : string representing the full path to the exe
 Args    : [optional] name of executable to set path to
           [optional] boolean flag whether or not warn when exe is not found

=cut

sub executable{
   my ($self, $exe,$warn) = @_;

   if( defined $exe ) {
     $self->{'_pathtoexe'} = $exe;
   }
   unless( defined $self->{'_pathtoexe'} ) {
       my $prog_path = $self->program_path;
       if( $prog_path && -e $prog_path && -x $prog_path ) {
           $self->{'_pathtoexe'} = $prog_path;
       } else {
           my $exe;
           if( ( $exe = $self->io->exists_exe($self->program_name) ) &&
               -x $exe ) {
               $self->{'_pathtoexe'} = $exe;
           } else {
               $self->warn("Cannot find executable for ".$self->program_name) if $warn;
               $self->{'_pathtoexe'} = undef;
           }
       }
   }
   $self->{'_pathtoexe'};
}

=head2 program_path

 Title   : program_path
 Usage   : my $path = $factory->program_path();
 Function: Builds path for executable 
 Returns : string representing the full path to the exe
 Args    : none

=cut

sub program_path {
    my ($self) = @_;
    my @path;
    push @path, $self->program_dir if $self->program_dir;
    push @path, $self->program_name.($^O =~ /mswin/i ?'.exe':'');

    return File::Spec->catfile(@path);
}

=head2 program_dir

 Title   : program_dir
 Usage   : my $dir = $factory->program_dir();
 Function: Abstract get method for dir of program. To be implemented
           by wrapper.
 Returns : string representing program directory 
 Args    : none 

=cut

sub program_dir {
    my ($self) = @_;
    $self->throw_not_implemented();
}

=head2 program_name

 Title   : program_name
 Usage   : my $name = $factory->program_name();
 Function: Abstract get method for name of program. To be implemented
           by wrapper.
 Returns : string representing program name
 Args    : none

=cut

sub program_name {
    my ($self) = @_;
    $self->throw_not_implemented();
}

sub DESTROY {
    my $self= shift;
    unless ( $self->save_tempfiles ) {
	$self->cleanup();
    }
    $self->SUPER::DESTROY();
}


1;
