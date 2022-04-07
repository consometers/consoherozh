package smarthome.automation;

public enum ChartViewEnum {
	day,
	month,
	year;

	boolean isMonthOrYear()
	{
		return ((this == month) || (this == year));
	}
}
