package com.example.consolefilter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class LogFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();

        for (String keyword : ConsoleFilter.filterKeyword) {
            if (message.contains(keyword)) {
                return Result.DENY;
            }
        }

        return Result.NEUTRAL;
    }
}
