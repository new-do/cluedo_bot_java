package hu.karandi.cluedo.bot;

import static hu.karandi.cluedo.bot.CluedoApi.Location.Billiards;
import static hu.karandi.cluedo.bot.CluedoApi.QuestionType.Interrogation;
import static hu.karandi.cluedo.bot.CluedoApi.Suspect.ColMustard;
import static hu.karandi.cluedo.bot.CluedoApi.Weapon.Revolver;
import static java.lang.System.out;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.List;

import lombok.Data;
import lombok.Value;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CluedoApi {
	
	public static interface Card {
		String name();
	}
	
	public static enum Weapon implements Card {
		Candlestick,
		IcePick,
		Poison,
		Poker,
		Revolver,
		Shears
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
	
	@Value
	public static class Question {
		private Weapon weapon;
		private Location location;
		private Suspect person;
	}

	@Value
	public static class Asked {
		private String by;
		private Question question;
	}

	public static enum AnswerState {
		No,
		Yes,
		YesButHidden
	}
	
	@Value
	public static class Answered {
		private String by;
		private AnswerState answeredWith;
		private Card answer;
	}

	public interface Round { }

	@Value
	public static class Accusation implements Round {
	    private Asked accused;
	    private Boolean answer;
	}

	@Value
	public static class Interrogation implements Round {
	    private Asked asked;
	    private List<Answered> answered;
	}

	@Data
	public static class OwnCards {
		private List<Weapon> weapons;
		private List<Location> locations;
		private List<Suspect> suspects;
	}

	@Value
	public static class CluedoRequest {
		private OwnCards myCards;
		private List<Round> rounds;
	}

	public static enum QuestionType {
		Interrogation,
		Accusation
	}

	@Data
	private static class AskQuestionResponse {
		private final QuestionType type;
		private final Question question;
	}

	@Data
	public static class GiveAnswerResponse {
		private final Card card;
	}

	public static void main(String[] args) {
		port(Integer.parseInt(args[0]));
		Gson gson = new Gson();

		post("/askquestion", (req, res) -> {//TODO: implement me
			CluedoRequest payload = gson.fromJson(req.body(), CluedoRequest.class);
			out.println("Ask question " + payload);
			return new AskQuestionResponse(Interrogation, new Question(Revolver, Billiards, ColMustard));
		}, gson::toJson);

		post("/giveanswer", (req, res) -> {//TODO: implement me
			CluedoRequest payload = gson.fromJson(req.body(), CluedoRequest.class);
			out.println("Give answer " + payload);
			return new GiveAnswerResponse(null);
		}, gson::toJson);

		after((req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			res.type("application/json");
		});

		exception(JsonSyntaxException.class, (ex, req, res) -> {
		    ex.printStackTrace();
			res.status(400);
		    res.body("Wrong input");
		});
	}
}
