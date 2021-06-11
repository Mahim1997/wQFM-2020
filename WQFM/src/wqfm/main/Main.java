package wqfm.main;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import wqfm.algo.FMRunner;
import wqfm.bip.WeightedPartitionScores;
import wqfm.configs.Config;
import wqfm.configs.DefaultValues;
import wqfm.feature.Bin;
import wqfm.utils.AnnotationsHandler;
import wqfm.utils.Helper;
import wqfm.utils.InputFileHandler;
import wqfm.utils.QuartetScoreHandler;
import wqfm.utils.TreeHandler;

@CommandLine.Command(name = "wQFM", mixinStandardHelpOptions = true, version = Main.WQFM_VERSION,
        description = "Runing " + Main.WQFM_VERSION)
public class Main implements Callable<Integer> {

    public static boolean DEBUG_MODE = false;

    //    public static String INPUT_FILE_NAME = "input_files/weighted_quartets_avian_biological_dataset";
    public static String INPUT_FILE_NAME_PLACE_HOLDER = "input_files/wqrts_11Tax_est_5G_R1";
    public static String OUTPUT_FILE_NAME_PLACE_HOLDER = "test-output-file-wqfm-java.tre";
    public static String SPECIES_TREE_FILE_NAME = Config.OUTPUT_FILE_NAME; // for now both will be the same

    public static final String WQFM_VERSION = "wQFM v1.3";
    public static String PYTHON_ENGINE = "python3";

//    @CommandLine.Option(names = {"-i", "--input_file"}, required = false, description = "The input file name/path for weighted quartets")
    @CommandLine.Option(names = {"-i", "--input_file"}, required = true, description = "The input file name/path \n(default: for weighted quartets, see option -im/--input_mode for details)")
    private String inputFileName = INPUT_FILE_NAME_PLACE_HOLDER;

//    @CommandLine.Option(names = {"-o", "--output_file"}, required = false, description = "The output file name/path for (estimated) species tree")
    @CommandLine.Option(names = {"-o", "--output_file"}, required = false, description = "The output file name/path for (estimated) species tree")
    private String outputFileNameSpeciesTree = OUTPUT_FILE_NAME_PLACE_HOLDER;

    @CommandLine.Option(names = {"-t", "--annotations_level"}, required = false, description = "t=0 for none (default)\nt=1 for annotations using quartet support\nt=2 for annotations using quartet support normalized by sum\nt=3 for annotations using quartet support nomralized by max")
    private int annotationsLevel = DefaultValues.ANNOTATIONS_LEVEL0_NONE;

    @CommandLine.Option(names = {"-beta", "--partition_score_beta"}, required = false, description = "(default) beta = 1, and [s]-[v] used\nbeta='<BETA>' for 1[ws]-<BETA>[wv] partition score\nbeta=<dyanmic> then dynamic bin heuristic is used.")
    private String beta = "1"; //WeightedPartitionScores.BETA_PARTITION_SCORE;

    @CommandLine.Option(names = {"-st", "--species_tree"}, required = false, description = "If given, will run annotations and provide to output file (will NOT run wQFM)")
    private String speciesTreeFileName = DefaultValues.NULL;

    @CommandLine.Option(names = {"-pe", "--python_engine"}, required = false, description = "(default) python3\npv = python for simple python engine")
    private String pythonEngine = PYTHON_ENGINE;

    // for input file mode -> wqrts, gene_trees, qrts, sequences
    @CommandLine.Option(names = {"-im", "--input_mode"}, required = false, description = "im=<" + DefaultValues.INPUT_MODE_WEIGHTED_QUARTETS + ">" + " (default)\n" + "im=<" + DefaultValues.INPUT_MODE_GENE_TREES + ">" + " when input file consists of gene trees")
    private String inputFileMode = Config.INPUT_MODE;

    // for quartet score level
    @CommandLine.Option(names = {"-q", "--quartet_score_level"}, required = false, description = "q=0: do not show quartet score(default)\nq=1: show quartet score only\nq=2: show quartet score, total weight of quartets, proportion of quartets satisfied.")
    private int quartetScoreLevel = DefaultValues.QUARTET_SCORE_LEVEL_0_NONE;

    @CommandLine.Option(names = {"-qo", "--quartet_score_output_file"}, required = false, description = "(default) null\nIf given, quartet scores will be output here.")
    private String quartetScoreOutputFile = null;

    private static void goDebugMode() {
        Config.ANNOTATIONS_LEVEL = DefaultValues.ANNOTATIONS_LEVEL3_QUARTET_SUPPORT_NORMALIZED_MAX;
        Main.PYTHON_ENGINE = "python";
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        Config.INPUT_FILE_NAME = this.inputFileName;
        Config.OUTPUT_FILE_NAME = this.outputFileNameSpeciesTree;

        Main.SPECIES_TREE_FILE_NAME = Config.OUTPUT_FILE_NAME; // initially set as same.

        Config.ANNOTATIONS_LEVEL = (this.annotationsLevel <= DefaultValues.ANNOTATIONS_LEVEL3_QUARTET_SUPPORT_NORMALIZED_MAX)
                ? this.annotationsLevel
                : DefaultValues.ANNOTATIONS_LEVEL0_NONE;

        // set partition scores.
        double beta_double = DefaultValues.BETA_DEFAULT_VAL; // 1.0
        try {
            Bin.WILL_DO_DYNAMIC = false;
            beta_double = Double.parseDouble(this.beta);
            Config.PARTITION_SCORE_MODE = DefaultValues.PARITTION_SCORE_COMMAND_LINE;
            WeightedPartitionScores.BETA_PARTITION_SCORE = beta_double; // set beta
            System.out.println(WeightedPartitionScores.GET_PARTITION_SCORE_PRINT());

        } catch (NumberFormatException e) {
            if (this.beta.equals("dynamic")) {
                Bin.WILL_DO_DYNAMIC = true;
                Config.PARTITION_SCORE_MODE = DefaultValues.PARTITION_SCORE_FULL_DYNAMIC;
                System.out.println("Using full dyanmic heuristic for partition score computation");
            }
        }

        // print annotations message.
        System.out.println(AnnotationsHandler.GET_ANNOTATIONS_LEVEL_MESSAGE());

        Main.PYTHON_ENGINE = this.pythonEngine;
        System.out.println("Python Engine: " + Main.PYTHON_ENGINE);

        // debug mode.
        if (Main.DEBUG_MODE) {
            Main.goDebugMode();
        }

        Config.NORMALIZE_DUMMY_QUARTETS = true; // always will be used as true.

        // Check the mode of input file and do pre-processing accordingly.
        switch (this.inputFileMode) {
            case DefaultValues.INPUT_MODE_WEIGHTED_QUARTETS:
                // do nothing
                break;
            case DefaultValues.INPUT_MODE_GENE_TREES:
                System.out.println("Input file consists of gene trees ... generating weighted quartets to file: " + DefaultValues.INPUT_FILE_NAME_WQRTS_DEFAULT);
                // convert to weighted quartets using the script, dump into default wqrts name (input_file, output_file_
                InputFileHandler.generateWeightedQuartets(Config.INPUT_FILE_NAME, DefaultValues.INPUT_FILE_NAME_WQRTS_DEFAULT);

                // remove file generated temporarily
                Helper.removeFile(DefaultValues.TEMP_WEIGHTED_QUARTETS_FILE_TO_REMOVE);

                System.out.println("Generating weighted quartets completed.");
                // then switch to input file name as default weighted quartets name.
                Config.INPUT_FILE_NAME = DefaultValues.INPUT_FILE_NAME_WQRTS_DEFAULT;
        }

        // quartet score level and output file modes.
        Config.QUARTET_SCORE_LEVEL = this.quartetScoreLevel;
        Config.QUARTET_SCORE_OUTPUT_FILE = this.quartetScoreOutputFile;

        // java -jar wQFM.jar -i <input_wqrts> -st <sTree_reference> -q <quartet_score_level> -qo <quartet_score_output_file>
        // java -jar wQFM.jar -i <input_wqrts> -st <sTree_reference> -t <annotations_level> -q <quartet_score_level> -qo <quartet_score_output_file>
        // java -jar wQFM.jar -i <input_wqrts> -o <sTree_Output> -q <quartet_score_level> -qo <quartet_score_output_file>
        System.out.println("Quartet Score level: " + Config.QUARTET_SCORE_LEVEL);

        String treeOutput; // keep output tree here.

        if (this.speciesTreeFileName.equals(DefaultValues.NULL) == false) { // argument is passed.
            Main.SPECIES_TREE_FILE_NAME = this.speciesTreeFileName; // will be passed as the 2nd argument for python commands.
            treeOutput = Helper.getTreeFromFile(this.speciesTreeFileName);
        } else {
            treeOutput = Main.runwQFM(); // run wQFM
        }

        // Handle Annotations according to -t/--annotations_level
        AnnotationsHandler.handleAnnotations(treeOutput);

        // Handle Quartet Score according to -q/--quartet_score_level and -qo/--quartet_score_output_file
        QuartetScoreHandler.handleQuartetScore(Main.SPECIES_TREE_FILE_NAME, Config.QUARTET_SCORE_LEVEL, Config.QUARTET_SCORE_OUTPUT_FILE);
        
        return 0;
    }

    public static String runwQFM() {
        // Call wQFM runner here. ?
        System.out.println("================= **** ========== Running " + WQFM_VERSION + " ============== **** ====================");

        long time_1 = System.currentTimeMillis(); //calculate starting time

        Main.testIfRerootWorks(); // test to check if phylonet jar is attached correctly.

        String tree = FMRunner.runFunctions(); //main functions for wQFM

        long time_del = System.currentTimeMillis() - time_1;
        long minutes = (time_del / 1000) / 60;
        long seconds = (time_del / 1000) % 60;
        System.out.format("\nTime taken = %d ms ==> %d minutes and %d seconds.\n", time_del, minutes, seconds);
        System.out.println("================= **** ======================== **** ====================");

        return tree;
    }

    private static void testIfRerootWorks() {
        try {
            //Test a dummy reroot function. To check if "lib" is in correct folder.
            String newickTree = "((3,(1,2)),((6,5),4));";
            String outGroupNode = "5";
            TreeHandler.rerootTree(newickTree, outGroupNode);
        } catch (Exception e) {
            System.out.println("Reroot not working, check if lib is in correct folder. Exiting.");
            System.exit(-1);
        }
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
