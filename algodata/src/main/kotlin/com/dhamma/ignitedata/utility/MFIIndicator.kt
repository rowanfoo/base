package com.dhamma.ignitedata.utility


import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.CachedIndicator
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator
import org.ta4j.core.num.Num

class MFIIndicator(
    timeSeries: TimeSeries,
    private val typicalPriceIndicator: TypicalPriceIndicator,
    private val timeFrame: Int
) : CachedIndicator<Num>(typicalPriceIndicator) {

    private var raw_money_flow = 0.0
    private var positive_flow = 0.0
    private var negative_flow = 0.0
    private var money_flow_ratio = 0.0
    private var money_flow_index = 0.0
    override fun calculate(index: Int): Num {
        raw_money_flow = 0.0
        positive_flow = 0.0
        negative_flow = 0.0
        money_flow_ratio = 0.0
        money_flow_index = 0.0
        for (i in index downTo index - (timeFrame - 1)) {
            try {
                raw_money_flow =
                    typicalPriceIndicator.getValue(i).multipliedBy(timeSeries.getBar(i).volume).doubleValue()
                if (typicalPriceIndicator.getValue(i).isGreaterThan(typicalPriceIndicator.getValue(i - 1))) {
                    positive_flow = positive_flow + raw_money_flow
                    //Log.d("<< P flow", String.valueOf(raw_money_flow));
                } else if (typicalPriceIndicator.getValue(i).isLessThan(typicalPriceIndicator.getValue(i - 1))) {
                    negative_flow = negative_flow + raw_money_flow
                    //Log.d("<< N flow", String.valueOf(raw_money_flow));
                }
            } catch (ex: IndexOutOfBoundsException) {
//                Log.d("<< IndexOutOfBound", ex.toString())
                println("<< IndexOutOfBound ${ex.toString()} ")
            }
        }
        money_flow_ratio = positive_flow / negative_flow
        money_flow_index = 1 + money_flow_ratio
        money_flow_index = 100 / money_flow_index
        money_flow_index = 100 - money_flow_index
        return this.numOf(money_flow_index)
    }
}
