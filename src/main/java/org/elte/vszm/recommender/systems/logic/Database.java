package org.elte.vszm.recommender.systems.logic;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.elte.vszm.recommender.systems.data.*;
import org.springframework.stereotype.Component;

@Component
public class Database {

	private Map<Integer, Movie> movieMap;
	private Map<Integer, User> userMap;


	@PostConstruct
	public void initialize() throws IOException {

		this.movieMap =
			StreamSupport.stream(CSVFormat.DEFAULT
				.parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ml-1m/movies.dat")))
				.spliterator(), true)
				.map
					(record -> Movie.builder()
						.number(Integer.valueOf(record.get(0)))
						.title(record.get(1))
						.categories(Arrays.asList(record.get(2).split("\\|")))
						.build()).collect(Collectors.toMap(movie -> movie.getNumber(), Function.identity()));

		this.userMap =
			StreamSupport.stream(CSVFormat.DEFAULT.parse(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ml-1m/users.dat")))
				.spliterator(), true)
				.map
					(record -> User.builder()
						.number(Integer.valueOf(record.get(0)))
						.gender(record.get(1).charAt(0))
						.ageCategory(AgeCategory.fromString(record.get(2)))
						.occupation(Occupation.fromString(record.get(3)))
						.zipCode(record.get(4))
						.build()).collect(Collectors.toMap(user -> user.getNumber(), Function.identity()));

	}


	public User getUser(Integer id) {
		return userMap.get(id);
	}

	public Movie getMovie(Integer id) {
		return movieMap.get(id);
	}


}
