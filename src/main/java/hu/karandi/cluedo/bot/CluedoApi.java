package hu.karandi.cluedo.bot;

import static java.lang.System.out;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.List;

import lombok.Data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class CluedoApi {
	
	private static final String GAME_ID = "gameId";
	
	public static interface Card {
		String name();
	}
	
	public static enum Location implements Card {
		Kitchen,
		BallRoom,
		Conservatory,
		DiningRoom,
		BilliardRoom,
		Library,
		Lounge,
		Hall,
		Study
	}

	public static enum Suspect implements Card {
		MsScarlett,
		ProfPlum,
		MrsPeacock,
		RevGreen,
		ColMustard,
		MrsWhite
	}
	
	public static enum Weapon implements Card {
		Candlestick,
		Dagger,
		LeadPipe,
		Revolver,
		Rope,
		Spanner
	}
	
	public static enum QuestionType {
		Question,
		Accusation
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
	private static class InformPayload {
		private final int askedBy;
		private final Question question;
		private final int answeredBy;
//		private final Card answer;
	}
	
	public static void main(String[] args) {
		port(8001);
		Gson gson = new Gson();
		
		get("/name", (req, res) -> "Team Alpha"); //TODO Introduce yourself
		post("/:" + GAME_ID + "/startGame", (req, res) -> { //TODO Don't cheat
//			out.println("Start game " + req.params(GAME_ID)); 
//			StartGamePayload payload = gson.fromJson(req.body(), StartGamePayload.class);
//			out.println(payload.getCountOfPlayers() + "\t" + payload.getPlayerId());
//			payload.getWeapons().forEach(out::println);
//			payload.getLocations().forEach(out::println);
//			payload.getSuspects().forEach(out::println);
			return "OK";
		});
		post("/:" + GAME_ID + "/giveAnswer", (req, res) -> { //TODO Don't cheat
//			out.println("Give answer " + req.params(GAME_ID)); 
//			GiveAnswerPayload payload = gson.fromJson(req.body(), GiveAnswerPayload.class);
//			out.print(payload.getAskedBy() + "\t" + payload.getQuestion());
			return ""; 
		});
		post("/:" + GAME_ID + "/askQuestion", (req, res) -> { //TODO Play in 3; Play in 6
//			out.println("Ask question " + req.params(GAME_ID));
			return new Question(QuestionType.Accusation, Location.BilliardRoom, Suspect.ColMustard, Weapon.LeadPipe);
		}, gson::toJson);
		post("/:" + GAME_ID + "/observe", (req, res) -> { //TODO Play in 3; Play in 6
//			out.println("Observe " + req.params(GAME_ID));
//			InformPayload payload = gson.fromJson(req.body(), InformPayload.class);
//			out.println(payload.getAskedBy() + "\t" + payload.getQuestion() + " \t" + payload.getAnsweredBy()); // + "\t" + payload.getAnswer());
			return "OK";
		});
		
		exception(JsonSyntaxException.class, (ex, req, res) -> {
		    ex.printStackTrace();
			res.status(400);
		    res.body("Wrong input");
		});
	}
}
