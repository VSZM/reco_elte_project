package org.elte.vszm.recommender.systems.logic;


import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.*;
import org.elte.vszm.recommender.systems.communication.MovieRecommendation;
import org.elte.vszm.recommender.systems.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.eval.RecommenderEvaluator;
import net.librec.eval.rating.MAEEvaluator;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.recommender.*;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.*;

@Component
@DependsOn("database")
public class RecommenderEngine {

	private static final Logger LOGGER = LogManager.getLogger();

	@Autowired
	private Database database;

	private Recommender recommender;

	@PostConstruct
	public void initializeRecommender() throws LibrecException {
		// build data model
		Configuration conf = new Configuration();
		conf.addDefaultResource("librec.properties");
		DataModel dataModel = new TextDataModel(conf);
		dataModel.buildDataModel();

		// build recommender context
		RecommenderContext context = new RecommenderContext(conf, dataModel);

		// build similarity matrix
		RecommenderSimilarity similarity = new CosineSimilarity();// TODO: Try different similarities
		similarity.buildSimilarityMatrix(dataModel);
		context.setSimilarity(similarity);

		// build recommender
		recommender = new UserKNNRecommender();
		recommender.setContext(context);

		// run recommender algorithm
		recommender.recommend(context);

		// evaluate the recommended result
		RecommenderEvaluator evaluator = new MAEEvaluator();
		LOGGER.info("Root mean squared error of the algorithm: |{}|", recommender.evaluate(evaluator));

		LOGGER.info("Listing the top5 results for user number 5");
		List<MovieRecommendation> movieRecommendationsForUser5 = getMovieRecommendationsForUser("5", 5);
		for (MovieRecommendation movieRecommendation : movieRecommendationsForUser5) {
			LOGGER.info(movieRecommendation);
		}



		// set id list of filter
		List<String> userIdList = new ArrayList<>();
		userIdList.add("5");

		// filter the recommended result
		List<RecommendedItem> recommendedItemList = recommender.getRecommendedList();
		GenericRecommendedFilter filter = new GenericRecommendedFilter();
		filter.setUserIdList(userIdList);
		recommendedItemList = filter.filter(recommendedItemList);

		// print filter result
		for (RecommendedItem recommendedItem : recommendedItemList) {
			LOGGER.info("Recommending for user |{}| the item |{}| with value |{}|",
				database.getUser(Integer.valueOf(recommendedItem.getUserId())),
				database.getMovie(Integer.valueOf(recommendedItem.getItemId())),
				recommendedItem.getValue());
		}
	}

	public List<MovieRecommendation> getMovieRecommendationsForUser(String externalId, Integer maxResults) {
		List<RecommendedItem> recommendedList = recommender.getRecommendedList();
		GenericRecommendedFilter filter = new GenericRecommendedFilter();
		filter.setUserIdList(Lists.newArrayList(externalId));
		return filter.filter(recommendedList).stream().sorted(new Comparator<RecommendedItem>() {
			@Override
			public int compare(RecommendedItem left, RecommendedItem right) {
				return -1 * Double.compare(left.getValue(), right.getValue());
			}
		}).limit(maxResults).map(recommendedItem -> {
			Movie movie = database.getMovie(Integer.valueOf(recommendedItem.getItemId()));

			return MovieRecommendation.builder()
				.title(movie.getTitle())
				.externalId(movie.getNumber())
				.id(movie.getNumber())
				.score(recommendedItem.getValue())
				.type("Movie")
				.build();
		}).collect(Collectors.toList());
	}
}
