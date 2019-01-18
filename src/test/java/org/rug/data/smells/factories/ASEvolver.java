package org.rug.data.smells.factories;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rug.data.labels.VertexLabel;
import org.rug.data.smells.ArchitecturalSmell;
import org.rug.data.smells.CDSmell;


import java.util.*;
import java.util.function.Supplier;

/**
 * Evolves a system by adding, removing and modifying smells.
 */
public abstract class ASEvolver {

    protected Graph system;
    protected GraphTraversalSource g;
    protected Random rng;

    protected ASEvolver(Graph system) {
        this.system = system;
        this.g = system.traversal();
        this.rng = new Random();
    }


    /**
     * Adds a smell to the given set of vertices. The type of the smell depends on the implementation
     * @param vertices the vertices target
     */
    public abstract void addSmell(Set<Vertex> vertices);

    /**
     * Removes the smell from the system
     * @param smell a vertex with label VertexLabel.Smell
     */
    public void removeSmell(ArchitecturalSmell smell){
        g.V(smell.getSmellNodes()).drop().iterate();
    }

    /**
     * Add the given amount of elements to the given smell.
     * @param smell the smell to enlarge
     * @param n the number of nodes to add. Some smell types might support addition to multiple parts.
     */
    public abstract void addElements(ArchitecturalSmell smell, int... n);

    /**
     * Remove the given amount of elements from the given smell. If the number of elements exceeds the minimum
     * number of elements necessary for the smell to exists, then the smell is removed.
     * @param smell the smell to reduce
     * @param n the number of nodes to add. Some smell types might support addition to multiple parts.
     */
    public abstract void removeElements(ArchitecturalSmell smell, int... n);

    /**
     * Valid only for smells that have a shape attribute.
     * Changes the shape of a smell to the shape managed by this instance.
     * @param smell the smell to shapeshift.
     */
    public abstract void shapeShift(CDSmell smell);

    /**
     * Returns a single vertex (package or classes) that is not affected by a smell and excludes a given amount of nodes.
     * @param exclude (optional) a list of vertices to exclude from the result
     * @return a vertex not affected by any smell
     */
    public Vertex getVertexNotAffectedBySmell(Vertex... exclude){
        return (Vertex)getVerticesNotAffectedBySmell(1, exclude).toArray()[0];
    }

    /**
     * Returns a set of vertices (package or classes) that are not affected by a smell and excludes a given amount of nodes.
     * @param n the number of nodes to return
     * @param exclude (optional) a list of vertices to exclude from the result
     * @return a set containing the vertices
     */
    public Set<Vertex> getVerticesNotAffectedBySmell(int n, Vertex... exclude) {
        Set<Vertex> vertices;

        Supplier<Set<Vertex>> supplyVertices = () -> {
            Set<Vertex> svertices = new HashSet<>();
            for (int i = 0; i < n; i++) {
                svertices.add(g.addV(VertexLabel.PACKAGE.toString())
                        .property("name", String.format("%s.%s", "org.dummy.vertex", Math.round(Math.random() * 100000))).next());
            }
            return svertices;
        };

        try{
            if (exclude.length > 0)
                vertices = g.V().hasLabel(P.within(VertexLabel.CLASS.toString(), VertexLabel.PACKAGE.toString()))
                        .not(__.in().hasLabel(VertexLabel.SMELL.toString()))
                        .is(P.not(P.within(exclude)))
                        .sample(n).toSet();
            else
                vertices = g.V().hasLabel(P.within(VertexLabel.CLASS.toString(), VertexLabel.PACKAGE.toString()))
                        .not(__.in().hasLabel(VertexLabel.SMELL.toString()))
                        .sample(n).toSet();

        }catch (NoSuchElementException e){
            vertices = new HashSet<>();
        }

        return vertices.size() == 0 ? supplyVertices.get() : vertices;
    }
}