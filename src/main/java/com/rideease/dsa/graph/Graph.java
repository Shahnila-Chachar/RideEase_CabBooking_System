package com.rideease.dsa.graph;

import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    private static class Edge {
        String destination;
        double distance;

        Edge(String destination, double distance) {
            this.destination = destination;
            this.distance = distance;
        }
    }

    private static class NodeDistance {
        String node;
        double distance;

        NodeDistance(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addRoad(String first, String second, double distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive.");
        }

        addLocation(first);
        addLocation(second);

        adjacencyList.get(first).add(new Edge(second, distance));
        adjacencyList.get(second).add(new Edge(first, distance));
    }

    public Route shortestPath(String source, String destination) {
        if (!adjacencyList.containsKey(source)
                || !adjacencyList.containsKey(destination)) {
            return new Route(Double.POSITIVE_INFINITY, Collections.emptyList());
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        for (String location : adjacencyList.keySet()) {
            distances.put(location, Double.POSITIVE_INFINITY);
        }

        PriorityQueue<NodeDistance> queue =
                new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        distances.put(source, 0.0);
        queue.add(new NodeDistance(source, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();

            if (current.distance > distances.get(current.node)) {
                continue;
            }

            if (current.node.equals(destination)) {
                break;
            }

            for (Edge edge : adjacencyList.get(current.node)) {
                double newDistance = current.distance + edge.distance;

                if (newDistance < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDistance);
                    previous.put(edge.destination, current.node);
                    queue.add(new NodeDistance(edge.destination, newDistance));
                }
            }
        }

        double totalDistance = distances.get(destination);

        if (Double.isInfinite(totalDistance)) {
            return new Route(totalDistance, Collections.emptyList());
        }

        List<String> path = new ArrayList<>();
        String current = destination;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);
        return new Route(totalDistance, path);
    }

    public static class Route {
        private final double distance;
        private final List<String> locations;

        public Route(double distance, List<String> locations) {
            this.distance = distance;
            this.locations = locations;
        }

        public double getDistance() {
            return distance;
        }

        public List<String> getLocations() {
            return locations;
        }

        public boolean isAvailable() {
            return !Double.isInfinite(distance);
        }
    }
}