package furgl.mobEvents.common.achievements;

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
		achievementItsFinallyOver = new Achievement("achievement.itsFinallyOver", "itsFinallyOver", 0, 0, Items.WOODEN_SWORD, null);
		achievementISurvived = new Achievement("achievement.iSurvived", "iSurvived", 2, 2, Items.STONE_SWORD, achievementItsFinallyOver);
		achievementThatWasEasy = new Achievement("achievement.thatWasEasy", "thatWasEasy", 4, 4, Items.IRON_SWORD, achievementISurvived);
		achievementExpert = new Achievement("achievement.expert", "expert", 6, 6, Items.DIAMOND_SWORD, achievementThatWasEasy);
		
		achievements.add(achievementItsFinallyOver);
		achievements.add(achievementISurvived);
		achievements.add(achievementThatWasEasy);
		achievements.add(achievementExpert);
	}
}
