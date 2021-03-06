package org.rug.args;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Args {

    @ParametersDelegate
    public ProjectArgsManager project = new ProjectArgsManager();

    @Parameter(names = {"-outputDir", "-o"}, description = "This name will be used to generate an outputDir directory where the outputDir will be saved.", required = true, converter = OutputDirManager.class)
    public File outputDir;

    @Parameter(names = {"-input", "-i"}, description = "The input directory containing the input files (either sources/binaries or graphs).", required = true, converter = InputDirManager.class)
    public File inputDirectory;

    @Parameter(names = {"-runArcan", "-rA"}, description = "Analyse files with Arcan. This parameter shall point to the JAR containing Arcan, without any parameters. Ex. ./path/to/Arcan.jar.")
    private String runArcan = null;

    @Parameter(names = {"-doNotRunTracker", "-dRT"}, description = "Do not execute the tracking algorithm runner.")
    private boolean disableTrackerRunner = false;

    @Parameter(names = {"-runProjectSize", "-rS"}, description = "Whether to run the project size runner.")
    private boolean runProjectSizes = false;

    @Parameter(names = {"-runFanInFanOut", "-rF"}, description = "Whether to run the fan in and fan out counter.")
    private boolean runFanInFanOutCounter = false;

    @Parameter(names = {"-showArcanOutput", "-sAO"}, description = "Whether or not to show Arcan's output to the console.")
    public boolean showArcanOutput = false;

    @Parameter(names = {"-pSimilarity", "-pS"}, description = "Print similarity scores of the matched smells. This file is saved within the outputDir directory.")
    public boolean similarityScores = false;

    @Parameter(names = {"-pCharacteristics", "-pC"}, description = "Print the characteristics of the tracked smells for every analyzed version.")
    public boolean smellCharacteristics = false;

    @Parameter(names = {"-pCompoCharact", "-pCC"}, description = "Print the component characteristics/metrics for every analyzed version.")
    public boolean componentCharacteristics;

    @Parameter(names = {"-trackNonConsecSmells", "-tNCS"}, description = "If set greater than 0, this allows to link similar smells appearing in non-consecutive versions. This argument indicates the maximum number of versions to skip.")
    public int trackNonConsecutiveVersions = 0;

    @Parameter(names = {"--help", "-h", "-help", "-?"}, help = true)
    public boolean help;

    @Parameter(names = {"--verbose", "-v"}, description = "Enables debug-level logging.")
    public boolean verbose = false;

    @Parameter(names = {"-singleVersion", "-sv"}, description = "Running Arcan for the most recent version only.")
    public boolean singleVersion = false;

    public boolean runTracker(){ return !disableTrackerRunner; }

    public boolean runProjectSizes(){ return runProjectSizes; }

    public boolean runFanInFanOutCounter(){ return runFanInFanOutCounter; }

    public String getArcanJarFile(){
        return new File(runArcan).getAbsolutePath();
    }

    public boolean runArcan(){
        return runArcan != null;
    }

    public String getSimilarityScoreFile(){
        return getOutputFileName("similarity-scores", "csv");
    }

    public String getSmellCharacteristicsFile(){
        return getOutputFileName("smell-characteristics", "csv");
    }

    public String getAffectedComponentsFile(){
        return getOutputFileName("affected-components", "csv");
    }

    public String getCondensedGraphFile(){
        return getOutputFileName("condensed-graph", "graphml");
    }

    public String getTrackGraphFileName(){
        return getOutputFileName("track-graph", "graphml");
    }

    public String getProjectSizesFile(){return getOutputFileName("project-sizes", "csv");}

    public String getFanInFanOutFile(){return getOutputFileName("fanin-fanout", "csv");}

    public String getComponentCharacteristicsFile(){
        return getOutputFileName("component-characteristics", "csv");
    }

    private String getOutputFileName(String name, String format){
        String fileName = String.format("%s-%s.%s", name, ( trackNonConsecutiveVersions > 0 ? "consecOnly" : "nonConsec"), format);
        return Paths.get(getTrackASOutDir(), fileName).toString();
    }

    public String getHomeProjectDirectory(){
        return inputDirectory.getAbsolutePath();
    }

    public void adjustProjDirToArcanOutput(){
        inputDirectory = new InputDirManager().convert(Paths.get(outputDir.getAbsolutePath(), "arcanOutput", project.name).toAbsolutePath().toString());
    }

    public String getArcanOutDir(){
        Path p = Paths.get(outputDir.getAbsolutePath(), "arcanOutput", project.name);
        p.toFile().mkdirs();
        return p.toAbsolutePath().toString();
    }

    private String getTrackASOutDir(){
        Path p = Paths.get(outputDir.getAbsolutePath(), "trackASOutput", project.name);
        p.toFile().mkdirs();
        return p.toAbsolutePath().toString();
    }

    public void setGitRepo(File localRepo) {
        project.gitRepo = localRepo;
    }

    public File getGitRepo() {
        return project.gitRepo;
    }

    public boolean isGitProject(){
        return project.gitRepo != null;
    }

    public boolean isJavaProject() {
        return project.isJava;
    }

    public boolean shouldAnalyseSingleVersion() {
        return singleVersion;
    }
}
