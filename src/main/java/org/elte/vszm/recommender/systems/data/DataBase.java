package org.elte.vszm.recommender.systems.data;

import org.springframework.stereotype.Component;

import com.google.common.graph.*;

@Component
public class DataBase {

	private Network<GraphNode, GraphEdgeDirected> network = NetworkBuilder.directed()
		.allowsParallelEdges(true)
		.expectedNodeCount
			(11000)
		.expectedEdgeCount(1100000).build();




}
