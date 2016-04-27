package furgl.mobEvents.client.gui.achievements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;

public class Achievements 
{
	public static Achievement achievementItsFinallyOver;
	public static Achievement achievementISurvived;
	public static Achievement achievementThatWasEasy;
	public static Achievement achievementExpert;

	public static List<Achievement> achievements = new ArrayList<Achievement>();

	public static void init()
	{
		achievementItsFinallyOver = new Achievement("achievement.itsFinallyOver", "itsFinallyOver", 0, 0, Items.wooden_sword, null);
		achievementISurvived = new Achievement("achievement.iSurvived", "iSurvived", 2, 2, Items.stone_sword, achievementItsFinallyOver);
		achievementThatWasEasy = new Achievement("achievement.thatWasEasy", "thatWasEasy", 4, 4, Items.iron_sword, achievementISurvived);
		achievementExpert = new Achievement("achievement.expert", "expert", 6, 6, Items.diamond_sword, achievementThatWasEasy);
		
		achievements.add(achievementItsFinallyOver);
		achievements.add(achievementISurvived);
		achievements.add(achievementThatWasEasy);
		achievements.add(achievementExpert);
	}
}
