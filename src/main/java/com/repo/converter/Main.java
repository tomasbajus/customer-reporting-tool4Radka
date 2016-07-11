/*
 *  Copyright (C) 2016-2017 Tomas Bajus ICO: 04871774.
 */
package com.repo.converter;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.repo.converter.network.GoogleAnalyticsConnector;
import com.repo.converter.network.UrlBuilder;
import com.repo.converter.utils.FileUtils;

public class Main extends Application {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private String xlsReportFileName = null;
	private String gaUrlsFileName = null;

	FileUtils fileUtils = new FileUtils();

	@Override
	public void start(final Stage stage) {
		LOG.info("Application Started");
		stage.setTitle("Tool4Radka");
		fileUtils.mkOutputDir();

		//File Choosers setup
		final FileChooser fileChooser = new FileChooser();
		String homeDir = System.getProperty("user.home");
		fileChooser.setInitialDirectory(new File(homeDir));
		final FileChooser gaUrlsFileChooser = new FileChooser();
		String gaHomeDir = fileUtils.getOutputDirAbsolutePath();
		gaUrlsFileChooser.setInitialDirectory(new File(gaHomeDir));

		//Buttons
		final Button openXLSButton = new Button("Open XLS Report");
		final Button createGAUrlsButton = new Button("Create GA Urls");
		final Button sentUrlsToGoogle = new Button("Send report to GA");
		final Button openGAUrlsButton = new Button("Open GA Urls Button");

		openXLSButton.setOnAction(
				e -> {
					File file = fileChooser.showOpenDialog(stage);
					if (file != null) {
						xlsReportFileName = file.getAbsolutePath();
						try {
							Desktop.getDesktop().open(file);
						} catch (IOException e1) {
							LOG.error("Error occurred during opening xls file "+xlsReportFileName, e1);
						}
						LOG.info("File loaded "+xlsReportFileName);
					}
				});
		openGAUrlsButton.setOnAction(
				e -> {
					File file = gaUrlsFileChooser.showOpenDialog(stage);
					if (file != null) {
						gaUrlsFileName = file.getAbsolutePath();
						LOG.info("File loaded "+gaUrlsFileName);
						try {
							Desktop.getDesktop().open(file);
						} catch (IOException e1) {
							LOG.error("Error occurred during opening urls file " + gaUrlsFileName, e1);
						}
					} else {
						gaUrlsFileName = fileUtils.getGoogleAnalyticsFileName();
					}
				});

		createGAUrlsButton.setOnAction(
				e -> {
					GoogleAnalyticsConverter googleAnalyticsConverter = new GoogleAnalyticsConverter(xlsReportFileName);
					try {
						List<String> output = googleAnalyticsConverter.convert();
						LOG.info("Excel transformed");
						output.forEach(LOG::info);
						Path file = Paths.get(fileUtils.getGoogleAnalyticsFileName());
						Files.write(file, output, Charset.forName("UTF-8"));
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Export has been created " +fileUtils.getGoogleAnalyticsFileName(), ButtonType.OK);
						alert.showAndWait();
						if (alert.getResult() == ButtonType.OK) {
							alert.close();
						}
					} catch (Throwable throwable) {
						LOG.error("Error occurred during during converting of excel to query for Google analytics " + fileUtils.getGoogleAnalyticsFileName(), throwable);
					}
				});


		sentUrlsToGoogle.setOnAction(
				e -> {
					List<String> reports = new ArrayList<>();
					int counterOfFailedPosts = 0;
					GoogleAnalyticsConnector googleAnalyticsConnector = new GoogleAnalyticsConnector();
					try {
						Path file = Paths.get(gaUrlsFileName).toAbsolutePath();
						List<String> urlQueryParams = Files.readAllLines(file);
						for (String queryToSend : urlQueryParams) {
							reports.add(googleAnalyticsConnector.sendPost(UrlBuilder.HOST, queryToSend));
						}
					} catch (Throwable e1) {
						LOG.error("Error during sending urls to google analytics", e1);
					}

					LOG.info("Stuff sent to Google with following results :");
					reports.forEach(LOG::info);

					Path file = Paths.get(fileUtils.getGoogleAnalyticsReportFileName());
					try {
						Files.write(file, reports, Charset.forName("UTF-8"));

						for (String report : reports) {
							if (!report.contains("200")) {
								counterOfFailedPosts ++;
							}
						}
						String confirmation_text = "All events has been successfully posted to google analytics.";

						if (counterOfFailedPosts > 0) {
							if (counterOfFailedPosts == reports.size()) {
								confirmation_text = "NO EVENT was posted successfully to google analytics";
							} else if (counterOfFailedPosts < reports.size()/2) {
								confirmation_text = "More than half of the events has been successfully posted to google analytics";
							} else {
								confirmation_text = "Some events has not been posted successfully to google analytics";
							}
						}
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation_text+" See report : " +fileUtils.getGoogleAnalyticsReportFileName(), ButtonType.OK);
						alert.showAndWait();
						if (alert.getResult() == ButtonType.OK) {
							alert.close();
							if (counterOfFailedPosts > 0) {
								try {
									Desktop.getDesktop().open(new File(fileUtils.getGoogleAnalyticsReportFileName()));
								} catch (IOException e1) {
									LOG.error("Error during report processing", e1);
								}
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				});

		final GridPane inputGridPane = new GridPane();
		GridPane.setConstraints(openXLSButton, 0, 0);
		GridPane.setConstraints(createGAUrlsButton, 1, 0);
		GridPane.setConstraints(openGAUrlsButton, 0, 1);
		GridPane.setConstraints(sentUrlsToGoogle, 1, 1);
		inputGridPane.setHgap(6);
		inputGridPane.setVgap(6);
		inputGridPane.getChildren().addAll(openXLSButton,  createGAUrlsButton, openGAUrlsButton, sentUrlsToGoogle);


		final Pane rootGroup = new VBox(12);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(12, 12, 12, 12));

		stage.setScene(new Scene(rootGroup));
		stage.show();

	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
