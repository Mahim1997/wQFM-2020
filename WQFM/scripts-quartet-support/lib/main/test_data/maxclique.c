/* MaxClique - for finding maximal cliques and independent sets of graphs
 * Written by Kevin O'Neill
 * Based on algorithm of Tsukiyama, Ide, Ariyoshi, and Shirakawa
 * Latest version: December 18, 2003
 *
 * Copyright (C) 2003  Kevin O'Neill
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * The author can be contacted at oneill@cs.cornell.edu, or by mail at
 *   Computer Science Department
 *   4130 Upson Hall
 *   Cornell University
 *   Ithaca, NY 14853-7501
 */


/**
 * Standard includes
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>


/**
 * Internal constants
 */
#define MAX_MATRIX_SIZE 500
#define CLIQUE_MODE 0
#define IS_MODE 1
#define TRUE 1
#define FALSE 0
#define PROGRAM_NAME "maxclique"


/**
 * Main data structures
 */
int matrix_size;
int current_mode;
int adjacency_matrix[MAX_MATRIX_SIZE][MAX_MATRIX_SIZE];
int adjacency_lists[MAX_MATRIX_SIZE][MAX_MATRIX_SIZE];
int num_adjacencies[MAX_MATRIX_SIZE];
// Note: Bucket is a bit (0/1) array on the elements
// of the adjacency list for each node
int Bucket[MAX_MATRIX_SIZE][MAX_MATRIX_SIZE];
int IS[MAX_MATRIX_SIZE];


/**
 * Forward declarations
 */
char* load_matrix_with_floats_and_no_diagonal(int argc,char *argv[]);
void initialize_adjacency_lists_and_stuff();
void print_matrix();
void print_adjacency_lists();
void backtrack(int i, FILE *output);
void output_IS(FILE *output);


/**
 * Main method
 */
int main(int argc,char *argv[]) {
	
  char *output_name = argv[argc - 1];	
  char* error_string;
 
  error_string = load_matrix_with_floats_and_no_diagonal(argc - 1,argv);
  if (error_string != NULL) { 
    fprintf(stderr, "Invalid file: %s", error_string);
    fprintf(stderr, "\n --- \n\n");
    fprintf(stderr, "Command line format: %s [DATAFILE] [CUTOFF] [0/1] [OUTPUTFILE]\n", PROGRAM_NAME);
    fprintf(stderr, "Maximum matrix size is %d\n",MAX_MATRIX_SIZE);
    return 1;
  }

  // otherwise matrix read was successful, continue...

  // Initialize adjacency lists
  initialize_adjacency_lists_and_stuff();

  // Then go for it!

  FILE *output = fopen(output_name, "w");
  backtrack(0, output);
  fclose(output);

  return 0;

}


/**
 * Initialize matrix based on a matrix of floats, a cutoff value, and the mode (clique or IS)
 */
char* load_matrix_with_floats_and_no_diagonal(int argc,char *argv[]) {

  int i, j;
  float cutoff;
  float in_float;
  char * filename;
  FILE * datafile;
  int c;

  if (argc < 3) return "Too few command-line arguments!\n";
  filename = argv[1];
  cutoff = atof(argv[2]);
  current_mode = CLIQUE_MODE;
  if (argc == 4)
    if (atoi(argv[3]) == 1) current_mode = IS_MODE;
    
  // open text file for reading
  if ((datafile = fopen(filename,"r")) == NULL) 
    return "Unable to open data file\n";

  // read the size of the matrix (abort if no integer can be read)
  if (fscanf(datafile, "%d", &matrix_size) == 0) return "unable to read size of matrix\n";
  
  if (matrix_size > MAX_MATRIX_SIZE) 
    return "declared matrix size is too large.\n";

  in_float = 0;
  for(i=0;i<matrix_size;i++) 
    for(j=i+1;j<matrix_size;j++) {
      
      // read matrix value (abort if no float can be read)
      if (fscanf(datafile, "%f", &in_float) != 1)
	return "too few matrix entries, or invalid matrix format\n";

      // the following line is where we specify that items
      // are related if the pairwise value is *greater* 
      // than the cutoff
      adjacency_matrix[i][j] = (in_float > cutoff);
    }

  // as a precaution, make sure there isn't any data after the end of the matrix
  while ((c = fgetc(datafile)) != EOF) {
    if (!isspace(c)) 
      return "too many matrix entries, or invalid matrix format\n";
  }

  // close the file and return
  fclose(datafile);
  return NULL;

}


/**
 * Initialize adjacency lists, set all buckets to be empty, and set all IS values to 0
 *
 * Important note: Because the "backtrack" method finds independent sets, not cliques,
 * the algorithm needs to run on the "opposite/inverse" of the adjacency matrix. Thus the
 * "current_mode" (which inverts the graph when it's CLIQUE_MODE).
 */
void initialize_adjacency_lists_and_stuff() {

  int i,j;

  for(i=0;i<matrix_size;i++) num_adjacencies[i] = 0;
  for(i=0;i<matrix_size;i++) IS[i] = 0;

  for(i=0;i<matrix_size;i++) {
    for(j=i+1;j<matrix_size;j++) { // it's i+1, not i, because we ignore self-loops in the graph
      if (adjacency_matrix[i][j] == current_mode) {
	adjacency_lists[i][num_adjacencies[i]] = j;
	Bucket[i][num_adjacencies[i]] = 0;
	adjacency_lists[j][num_adjacencies[j]] = i;
	Bucket[j][num_adjacencies[j]] = 0;
	num_adjacencies[i]++;
	num_adjacencies[j]++;
      }
    }
  }

}


/**
 * For testing, to make sure the adjacency matrix looks like it does in the file
 */
void print_matrix() {

  int i,j;

  for(i=0;i<matrix_size;i++) {
    for(j=0;j<i;j++) fprintf(stdout,"  ");
    for(j=i;j<matrix_size;j++) {
      fprintf(stdout,"%d ", adjacency_matrix[i][j]);
    }
    fprintf(stdout,"\n");
  }

}


/**
 * Ditto for the adjacency lists
 */
void print_adjacency_lists() {

  int i,j;

  for(i=0;i<matrix_size;i++) {
    fprintf(stdout,"%d: ",i);
    for(j=0;j<num_adjacencies[i];j++) {
      fprintf(stdout,"%d ",adjacency_lists[i][j]);
    }
    fprintf(stdout,"\n");
  }

}


/**
 * This is the main method, taken directly from Tsukiyama et. al.
 */
void backtrack(int i, FILE *output) {

  int c, x;
  int f;

  int adjIndexY, y;
  int adjIndexZ, z;
  int bucketIndex;

  if (i >= (matrix_size-1)) {

    // Output new MIS designated by IS( )
    output_IS(output);

  } else {

    x = i+1;
    c = 0;

    // for y \in Adj(x) such that y \leq i
    for (adjIndexY=0; adjIndexY<num_adjacencies[x] && ((y=adjacency_lists[x][adjIndexY]) <= i); adjIndexY++) {
      if (IS[y] == 0) c++;
    }

    if (c == 0) {

      // for y \in Adj(x) such that y \leq i
      for (adjIndexY=0; adjIndexY<num_adjacencies[x] && ((y=adjacency_lists[x][adjIndexY]) <= i); adjIndexY++) {
	IS[y]++;
      }
      backtrack(x, output); 
      // for y \in Adj(x) such that y \leq i
      for (adjIndexY=0; adjIndexY<num_adjacencies[x] && ((y=adjacency_lists[x][adjIndexY]) <= i); adjIndexY++) {
	IS[y]--;
      }
      
      
    } else {

      IS[x] = c;
      backtrack(x, output);
      IS[x] = 0;
      f = TRUE;
      for (adjIndexY=0; adjIndexY<num_adjacencies[x] && ((y=adjacency_lists[x][adjIndexY]) <= i); adjIndexY++) {
	if (IS[y] == 0) {

	  // Put y in Bucket(x):
	  Bucket[x][adjIndexY] = 1;

	  // for z \in Adj(y) such that z \leq i
	  for (adjIndexZ=0; adjIndexZ<num_adjacencies[y] && ((z=adjacency_lists[y][adjIndexZ]) <= i); adjIndexZ++) {
	    IS[z]--;
	    if(IS[z] == 0) {
	      f = FALSE;
	    }
	  }
	}
	IS[y]++;
      }
      if (f) {
	backtrack(x, output);
      }
      for (adjIndexY=0; adjIndexY<num_adjacencies[x] && ((y=adjacency_lists[x][adjIndexY]) <= i); adjIndexY++) {
	IS[y]--;
      }

      // for y \in Bucket(x) do
      for (adjIndexY=0; adjIndexY<num_adjacencies[x]; adjIndexY++) {
	if (Bucket[x][adjIndexY]) {
	  y = adjacency_lists[x][adjIndexY];
	  for (adjIndexZ=0; adjIndexZ<num_adjacencies[y] && ((z=adjacency_lists[y][adjIndexZ]) <= i); adjIndexZ++) {
	    IS[z]++;
	  }

	  // delete y from Bucket(x)
	  Bucket[x][adjIndexY] = 0;

	}
      }

    }

  }

}


/**
 * This outputs a maximal clique/IS
 */
void output_IS(FILE *output) {

  int ISIndex;

   if (current_mode == CLIQUE_MODE) 
      fprintf(output,"Maximal clique: ");
    else
      fprintf(output,"Maximal independent set: ");

    for(ISIndex=0;ISIndex<matrix_size;ISIndex++) {
      // IS[v] = 0 iff v is in MIS (see paper)
      if (IS[ISIndex] == 0) fprintf(output,"%d ",ISIndex+1); 
      // ISIndex+1 so that node numbering starts with 1
    }    
    fprintf(output,"\n");

}

