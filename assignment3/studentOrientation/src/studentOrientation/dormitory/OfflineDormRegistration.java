package studentOrientation.dormitory;

import studentOrientation.activityExpenses.CaloriesI;
import studentOrientation.activityExpenses.CarbonCostI;
import studentOrientation.activityExpenses.CurrencyCostI;
import studentOrientation.activityExpenses.DurationI;

public class OfflineDormRegistration
		implements DormitoryRegistrationI, CaloriesI, CarbonCostI, CurrencyCostI, DurationI {

	private static final int AVG_REGISTRATION_DURATION = 60;
	private static final int AVG_CALORIES_SPENT = 80;
	private static final double AVG_CARBON_COST = 0.008;

	@Override
	public int getActivityDuration() {
		return AVG_REGISTRATION_DURATION;
	}

	@Override
	public double getCostInUSD() {

	}

	@Override
	public double getCarbonCostInTonnes() {
		return AVG_CARBON_COST;
	}

	@Override
	public int getCaloriesSpent() {
		return AVG_CALORIES_SPENT;
	}

	@Override
	public String getCommunityName() {
		// placeholder method
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRoomNumber() {
		// placeholder method
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
