package org.elte.vszm.recommender.systems.data;

import org.springframework.stereotype.Component;

import com.google.common.graph.*;

@Component
@Deprecated()// Not gonna use it for now. Will go with library use.
public class GraphDataBase {

	private Network<GraphNode, GraphEdgeDirected> network = NetworkBuilder.directed()
		.allowsParallelEdges(true)
		.expectedNodeCount
			(11000)
		.expectedEdgeCount(1100000).build();




}
