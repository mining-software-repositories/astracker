package org.rug.persistence;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rug.data.project.Project;
import org.rug.tracker.ASmellTracker;

import java.util.*;

public class SmellCharacteristicsGenerator extends CSVDataGenerator<ASmellTracker>{

    private List<String> header = new ArrayList<>();
    private Project project;

    public SmellCharacteristicsGenerator(String outputFile, Project project) {
        super(outputFile);
        this.project = project;
    }

    /**
     * Returns the header of the underlying data.
     *
     * @return a array containing the headers.
     */
    @Override
    public String[] getHeader() {
        return header.toArray(new String[0]);
    }

    /**
     * Accepts an object to serialize into a list of records.
     * This method's implementation must populate the {@link #records} protected attribute.
     *
     * @param object the object to serialize into records of strings.
     */
    public void accept(ASmellTracker object) {
        Graph simplifiedGraph = object.getCondensedGraph();
        GraphTraversalSource g = simplifiedGraph.traversal();

        Set<String> smellKeys = new TreeSet<>(g.V().hasLabel("smell").propertyMap().tryNext().orElse(Collections.emptyMap()).keySet());
        header.addAll(smellKeys);
        Set<String> characteristicKeys = new TreeSet<>();
        g.V().hasLabel("characteristic").forEachRemaining(v -> characteristicKeys.addAll(v.keys()));
        header.add("version");
        header.add("versionPosition");
        header.add("smellIdInVersion");
        header.addAll(characteristicKeys);

        Set<Vertex> smells = g.V().hasLabel("smell").toSet();
        smells.forEach(smell -> {
            List<String> commonRecord = new ArrayList<>();
            smellKeys.forEach(k -> commonRecord.add(smell.value(k).toString()));

            g.V(smell).outE("hasCharacteristic").as("e")
                    .inV().as("v")
                    .select("e", "v")
                    .forEachRemaining(variables -> {
                        Edge incomingEdge = (Edge)variables.get("e");
                        Vertex characteristic = (Vertex)variables.get("v");
                        List<String> completeRecord = new ArrayList<>(commonRecord);
                        String version = incomingEdge.value("version").toString();
                        completeRecord.add(version);
                        completeRecord.add(project.getVersionIndex(version).toString()); // TODO: change this with node property
                        completeRecord.add(incomingEdge.value("smellId").toString());
                        characteristicKeys.forEach(k -> completeRecord.add(characteristic.property(k).orElse("NA").toString()));
                        records.add(completeRecord);
                    });
        });
    }
}
