package io.github.jiashunx.masker.json.server.service;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;

/**
 * @author jiashunx
 */
public class ArgumentService {

    private static final int DEFAULT_LISTEN_PORT = 8080;

    private final CommandLine commandLine;

    public ArgumentService(String[] args) {
        CommandLineParser commandLineParser = new BasicParser();
        Options options = new Options();
        // -p 8080 | --port 8080
        options.addOption("p", "port", true, "server port, defualt: " + DEFAULT_LISTEN_PORT);
        try {
            this.commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("parse command line failed, args: %s", Arrays.asList(args)), e);
        }
    }

    public int getListenPort() {
        if (commandLine.hasOption('p')) {
            return Integer.parseInt(commandLine.getOptionValue('p'));
        }
        if (commandLine.hasOption("port")) {
            return Integer.parseInt(commandLine.getOptionValue("port"));
        }
        return DEFAULT_LISTEN_PORT;
    }

}
