package hu.karandi.cluedo.bot;

import static java.lang.System.out;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CluedoApi {
	
	private static final String GAME_ID = "gameId";
	
	public static interface Card {
		String name();
	}
	
	public static enum Location implements Card {
		BedRoom,
		Billiards,
		Conservatory,
		Kitchen,
		Library,
		Lounge,
		Stairs,
		Studio,
		TrophyHall
	}

	public static enum Suspect implements Card {
		ColMustard,
		MrsWhite,
		MsPeacock,
		MsScarlett,
		ProfPlum,
		RevGreen
	}
	
	public static enum Weapon implements Card {
		Candlestick,
		IcePick,
		Poison,
		Poker,
		Revolver,
		Shears
	}
	
	public static enum QuestionType {
		Interrogation,
		Accusation
	}

	@Data
	private static class BotDescription {
		private final String version;
	}

	@Data
	private static class Question {
		private final QuestionType type;
		private final Location location;
		private final Suspect suspect;
		private final Weapon weapon;
	}
	
	@Data
	private static class StartGamePayload {
		private final int playerId;
		private final int countOfPlayers;
		private final List<Weapon> weapons;
		private final List<Suspect> suspects;
		private final List<Location> locations;
	}
	
	@Data
	private static class GiveAnswerPayload {
		private final int askedBy;
		private final Question question;
	}
	
	@Data
	private static class Answer {
		private final Card card;
	}
	
	@Data
	private static class InformPayload {
		private final int askedBy;
		private final Question question;
		private final int answeredBy;
//		private final Card answer;
	}
	
	public static void main(String[] args) {
		port(Integer.parseInt(args[0]));
		final String teamName = args[1];
		Gson gson = new Gson();
		
		get("/name", (req, res) -> teamName); //TODO Introduce yourself
		post("/:" + GAME_ID + "/startGame", (req, res) -> { //TODO Don't cheat
			StartGamePayload payload = gson.fromJson(req.body(), StartGamePayload.class);
			List<Card> cards = new LinkedList<>();
			cards.addAll(payload.getWeapons());
			cards.addAll(payload.getLocations());
			cards.addAll(payload.getSuspects());
			String collect = cards.stream().map(card -> card.name()).collect(Collectors.joining(","));
			out.println(req.params(GAME_ID) 
					+ "\t" + "Start game "  
					+ " "+ payload.getCountOfPlayers() 
					+ " " + payload.getPlayerId()
					+ "\t"+ collect);
			return new BotDescription("0.1");
		}, gson::toJson);
		post("/:" + GAME_ID + "/giveAnswer", (req, res) -> { //TODO Don't cheat
			GiveAnswerPayload payload = gson.fromJson(req.body(), GiveAnswerPayload.class);
			out.println(req.params(GAME_ID) 
					+ "\t" + "Give answer "
					+ "\t" + payload.getAskedBy() 
					+ "\t" + payload.getQuestion());
			return new Answer(Suspect.ColMustard);
		}, gson::toJson);
		post("/:" + GAME_ID + "/askQuestion", (req, res) -> { //TODO Play in 3; Play in 6
//			out.println("Ask question " + req.params(GAME_ID));
			return new Question(QuestionType.Accusation, Location.Billiards, Suspect.ColMustard, Weapon.Revolver);
		}, gson::toJson);
		post("/:" + GAME_ID + "/observe", (req, res) -> { //TODO Play in 3; Play in 6
//			out.println("Observe " + req.params(GAME_ID));
//			InformPayload payload = gson.fromJson(req.body(), InformPayload.class);
//			out.println(payload.getAskedBy() + "\t" + payload.getQuestion() + " \t" + payload.getAnsweredBy()); // + "\t" + payload.getAnswer());
			return "OK";
		});
		after((req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
		});
		exception(JsonSyntaxException.class, (ex, req, res) -> {
		    ex.printStackTrace();
			res.status(400);
		    res.body("Wrong input");
		});
	}
}
