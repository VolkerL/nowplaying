package volker.streaming.music.gui;

/*
 * #%L
 * NowPlaying
 * %%
 * Copyright (C) 2013 Volker Lanting
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;

import volker.streaming.music.Config;
import volker.streaming.music.Formatter;
import volker.streaming.music.Track;
import volker.streaming.music.lastfm.LastFmApi;
import volker.streaming.music.lastfm.LastFmConfig;

public class ApiPanel extends JPanel {
	private static final long serialVersionUID = 6183608388616105271L;

	private static final Log LOG = LogFactory.getLog(ApiPanel.class);
	private static final String RUNNING = "Running...";
	private static final String STOPPED = "Stopped";
	
	private AtomicBoolean status = new AtomicBoolean(false);
	private JButton statusButton;
	private Border textBorder;
	private Border iconBorder;
	private JLabel statusLabel;
	private JLabel stateLabel;
	private ImageIcon onIcon;
	private ImageIcon offIcon;
	
	private final Config config;
	private final LastFmConfig lastfmConfig;
	private final Formatter formatter;
	
	public ApiPanel(Config config, LastFmConfig lastfmConfig, Formatter formatter) {
		this.config = config;
		this.lastfmConfig = lastfmConfig;
		this.formatter = formatter;
		initComponents();
		// FIXME keeps running until termination of jvm
		new Thread(new Poller()).start();
	}
	
	private void initComponents() {
		
		URL onUrl = getClass().getResource("on.png");
		onIcon = getIcon(onUrl);
		URL offUrl = getClass().getResource("off.png");
		offIcon = getIcon(offUrl);
		
		statusButton = new JButton();
		statusLabel = new JLabel("Start/Stop:");
		stateLabel = new JLabel(status.get() ? RUNNING : STOPPED);
		iconBorder = BorderFactory.createEmptyBorder();
		textBorder = statusButton.getBorder();
		statusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (status) {
					// status is only set here
					status.set(!status.get());
				}
				showUpdatedStatus();
			}
		});
		showUpdatedStatus();
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(statusLabel, GroupLayout.Alignment.CENTER)
				.addComponent(statusButton, GroupLayout.Alignment.CENTER)
				.addComponent(stateLabel, GroupLayout.Alignment.CENTER)
			)
			.addContainerGap()
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(statusLabel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(statusButton)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(stateLabel)
			.addContainerGap()
		);
	}
	
	private void showUpdatedStatus() {
		if (status.get()) {
			updateStatusButton(onIcon, "Currently running");
			stateLabel.setText(RUNNING);
		} else {
			updateStatusButton(offIcon, "Currently stopped");
			stateLabel.setText(STOPPED);
		}
	}
	
	// should be run on EDT
	private void updateStatusButton(ImageIcon icon, String backuptext) {
		if (icon == null) {
			statusButton.setText(backuptext);
			statusButton.setIcon(null);
			statusButton.setBorder(textBorder);
		} else {
			statusButton.setText("");
			statusButton.setIcon(icon);
			statusButton.setBorder(iconBorder);
		}
	}
	
	private ImageIcon getIcon(URL url) {
		ImageIcon result = null;
		if (url == null) {
			LOG.error("Couldn't find image");
		} else {
			try {
				result = new ImageIcon(ImageIO.read(url));
			} catch (IOException e) {
				LOG.error("Couldn't read image " + url.toExternalForm(), e);
			}
		}
		return result;
	}
	
	private class Poller implements Runnable {
		private LastFmApi api;
		public Poller() {
			api = new LastFmApi(lastfmConfig);
		}
		@Override
		public void run() {
			while (true) {
				if (status.get()) {
					Track t = api.getNowPlaying();
					File out = config.getOutputFile();
					if (out != null && !out.isDirectory()) {
						if (!out.isFile()) {
							try {
								out.createNewFile();
							} catch (SecurityException | IOException e) {
								LOG.error("Could not create outputfile", e);
							}
						}
						if (out.isFile()) {
							formatter.setNullMessage(config.getNoTrackMessage());
							String content = formatter.format(t, config.getFormat());
							OutputStreamWriter writer = null;
							try {
								writer = new OutputStreamWriter(new FileOutputStream(out, false), Consts.UTF_8);
								writer.write(content);
								writer.flush();
							} catch (IOException e) {
								LOG.error("Failed to write track to file", e);
							} finally {
								try {
									if(writer != null)
										writer.close();
								} catch (IOException e) {
									LOG.warn("Couldn't close filewriter", e);
								}
							}
						} else {
							LOG.warn("File " + out + "could not be created");
						}
					} else {
						LOG.warn("Invalid output file " + out);
					}
				}
				try {
					Thread.sleep(lastfmConfig.getPollInterval() * 1000);
				} catch (InterruptedException e) {
					// time to continue I guess
				}
			}
		}
	}
}
