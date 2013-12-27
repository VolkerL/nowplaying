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

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import volker.streaming.music.Config;
import volker.streaming.music.Formatter;
import volker.streaming.music.Track;
import volker.streaming.music.lastfm.LastFmConfig;

public class NowPlayingFrame extends JFrame {
	
	static {
		// TODO move this to a proper config file location
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("log4j.configuration", NowPlayingFrame.class.getResource("log4j.properties").toString());
	}
	
	private static final long serialVersionUID = 3566112284877911687L;
	private static final Log LOG = LogFactory.getLog(NowPlayingFrame.class);
	
	private final Track previewTrack;
	private final Formatter formatter;
	private ConfigPanel configPanel;
	private FormatPanel formatPanel;
	private ApiPanel apiPanel;
	private JButton saveButton;
	private JPanel savePanel;
	
	private final File cfgFile;
	private final LastFmConfig lastfmConfig;
	private final Config config;
	
	private final JFrame frame;
	
	private GroupLayout layout;
	
	public NowPlayingFrame(final File cfgFile) {
		this.cfgFile = cfgFile;

		frame = this;
		setTitle("NowPlaying - LastFM integration");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		
		lastfmConfig = LastFmConfig.fromFile(cfgFile);
		config = Config.fromFile(cfgFile);
		
		
		configPanel = new ConfigPanel(this, lastfmConfig);
		
		previewTrack = new Track("Beyonce", "Halo", "I Am... Sasha Fierce");
		formatter = new Formatter(Formatter.DEFAULT_EXCEPTIONS);
		formatPanel = new FormatPanel(this, config, formatter, previewTrack);
		
		apiPanel = new ApiPanel(config, lastfmConfig, formatter);
		
		saveButton = new JButton("Save configuration");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		savePanel = new JPanel(new GridBagLayout());
		savePanel.add(saveButton);
		
		layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(configPanel)
				.addComponent(formatPanel)
				.addComponent(apiPanel)
				.addComponent(savePanel)
			)
			.addContainerGap()
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(configPanel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(formatPanel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(apiPanel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(savePanel)
			.addContainerGap()
		);
		
		pack();
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        LastFmConfig storedLfm = LastFmConfig.fromFile(cfgFile);
		        Config stored = Config.fromFile(cfgFile);
		        if (!stored.contentEquals(config) || !storedLfm.contentEquals(lastfmConfig)) {
		        	int opt = JOptionPane.showConfirmDialog(
		        			frame,
		        			"You have unsaved configuration changes.\nDo you want to save them?",
		        			"Unsaved changes",
		        			JOptionPane.YES_NO_CANCEL_OPTION);
		        	switch (opt) {
		        	case JOptionPane.YES_OPTION :
		        		saveConfig();
		        	case JOptionPane.NO_OPTION :
		        		System.exit(0);
		        		break;
		        	case JOptionPane.CANCEL_OPTION :
		        		break;
		        	default:
		        		LOG.error("Unknown response to closing window");
		        	}
		        } else {
		        	System.exit(0);
		        }
		    }
		});
	}
	
	private void saveConfig() {
		if (cfgFile != null) {
			config.write(cfgFile, false);
			lastfmConfig.write(cfgFile, true);
		}
	}

	public static void main(String[] args) {
		String configFileName = "nowplaying.properties";
		URL configUrl = ClassLoader.getSystemResource(configFileName);
		if (configUrl == null) {
			LOG.warn("Failed to find config file " + configFileName);
		}
		File config = configUrl == null ? new File(configFileName) : new File(configUrl.getFile());
		
		final NowPlayingFrame frame = new NowPlayingFrame(config);
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
	}
}
