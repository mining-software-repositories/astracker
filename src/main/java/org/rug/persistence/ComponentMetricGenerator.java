package org.rug.persistence;

import org.rug.data.labels.VertexLabel;
import org.rug.data.project.Version;

import java.util.ArrayList;

/**
 * Writes component metrics on CSV files.
 */
public class ComponentMetricGenerator extends CSVDataGenerator<Version> {
    public ComponentMetricGenerator(String outputFile) {
        super(outputFile);
    }

    @Override
    public String[] getHeader() {
        return new String[]{"name", "type", "version", "versionPosition", "linesOfCode"};
    }

    @Override
    public void accept(Version version) {
        var g = version.getGraph().traversal();
        var versionString = version.getVersionString();
        var versionPosition = String.valueOf(version.getVersionPosition());
        g.V().hasLabel(VertexLabel.PACKAGE.toString(), VertexLabel.CLASS.toString())
                .has("linesOfCode")
                .forEachRemaining(vertex -> {
                    var record = new ArrayList<String>();
                    record.add(vertex.value("name"));
                    record.add(vertex.label());
                    record.add(versionString);
                    record.add(versionPosition);
                    record.add(vertex.value("linesOfCode").toString());
                    records.add(record);
                });
    }
}
