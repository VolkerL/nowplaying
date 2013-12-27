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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import volker.streaming.music.lastfm.LastFmConfig;

public class ConfigPanel extends JPanel {

	private static final long serialVersionUID = -7077776394208442006L;
	private static final Log LOG = LogFactory.getLog(ConfigPanel.class);
	
	private JLabel userLabel;
	private JTextField userField;
	
	private JLabel pollLabel;
	private JTextField pollField;
	private JLabel pollEndLabel;
	private JLabel pollErrorLabel;
	private Border pollBorder;
	private Border pollErrorBorder;
	
	private JLabel apiKeyLabel;
	private JTextField apiKeyField;
	
	private JLabel apiSchemeLabel;
	private JTextField apiSchemeField;
	
	private JLabel apiBaseLabel;
	private JTextField apiBaseField;
	
	private JLabel apiPathLabel;
	private JTextField apiPathField;
	
	private boolean advancedView = false; 
	private JCheckBox advancedViewBox;
	
	private JFrame parent;
	private LastFmConfig config;
	
	public ConfigPanel(JFrame parent, LastFmConfig config) {
		this.parent = parent;
		this.config = config;
		initComponents();
		initLayout();
	}
	
	private void initComponents() {
		userLabel = new JLabel("Last.fm user name:");
		userField = new JTextField(config.getUser(), 15);
		userField.getDocument().addDocumentListener(configUpdater);
		pollLabel = new JLabel("Update track info every");
		pollField = new JTextField(3);
		pollField.setText(String.valueOf(config.getPollInterval()));
		pollField.getDocument().addDocumentListener(configUpdater);
		pollField.getDocument().addDocumentListener(numberValidator);
		pollEndLabel = new JLabel("seconds");
		pollErrorLabel = new JLabel("Poll interval should be a valid integer.");
		pollErrorLabel.setForeground(Color.RED);
		pollBorder = pollField.getBorder();
		pollErrorBorder = BorderFactory.createLineBorder(Color.RED);
		pollErrorLabel.setVisible(false);

		apiSchemeLabel = new JLabel("Last.fm API scheme:");
		apiSchemeField = new JTextField(config.getApiScheme());
		apiSchemeField.getDocument().addDocumentListener(configUpdater);
		apiBaseLabel = new JLabel("Last.fm API base URL:");
		apiBaseField = new JTextField(config.getApiBase());
		apiBaseField.getDocument().addDocumentListener(configUpdater);
		apiPathLabel = new JLabel("Last.fm API path URL:");
		apiPathField = new JTextField(config.getApiPath());
		apiPathField.getDocument().addDocumentListener(configUpdater);
		apiKeyLabel = new JLabel("Last.fm API key:");
		apiKeyField = new JTextField(config.getApiKey());
		apiKeyField.getDocument().addDocumentListener(configUpdater);
		
		advancedViewBox = new JCheckBox("Display advanced configuration options", advancedView);
		advancedViewBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				advancedViewBoxListener(e);
			}
		});
	}
	private void initLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(userLabel)
							.addComponent(pollLabel)
							.addComponent(apiSchemeLabel)
							.addComponent(apiBaseLabel)
							.addComponent(apiPathLabel)
							.addComponent(apiKeyLabel)
						)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(userField)
							.addGroup(layout.createSequentialGroup()
								.addComponent(pollField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(pollEndLabel)
							)
							.addComponent(apiSchemeField)
							.addComponent(apiBaseField)
							.addComponent(apiPathField)
							.addComponent(apiKeyField)
						)
					)
					.addComponent(pollErrorLabel)
					.addComponent(advancedViewBox)
				)
				.addContainerGap()
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(userLabel)
					.addComponent(userField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(pollLabel)
					.addComponent(pollField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(pollEndLabel)
				)
				.addComponent(pollErrorLabel)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(advancedViewBox)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(apiSchemeLabel)
						.addComponent(apiSchemeField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(apiBaseLabel)
						.addComponent(apiBaseField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(apiPathLabel)
					.addComponent(apiPathField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(apiKeyLabel)
					.addComponent(apiKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addContainerGap()
		);
		
		updateView();
	}


	private void updateView() {
		apiSchemeLabel.setVisible(advancedView);
		apiSchemeField.setVisible(advancedView);
		apiBaseLabel.setVisible(advancedView);
		apiBaseField.setVisible(advancedView);
		apiPathLabel.setVisible(advancedView);
		apiPathField.setVisible(advancedView);
		apiKeyLabel.setVisible(advancedView);
		apiKeyField.setVisible(advancedView);
		parent.pack();
	}
	
	// action listener is on the EDT
	private void advancedViewBoxListener(ActionEvent evt) {
		advancedView = advancedViewBox.isSelected();
		updateView();
	}
	
	private DocumentListener configUpdater = new DocumentListener() {
		private void update() {
			if (userField.isShowing())
				config.setUser(userField.getText());
			if (pollField.isShowing()) {
				try {
					int i = Integer.parseInt(pollField.getText());
					config.setPollInterval(i);
				} catch (NumberFormatException ex) {
					LOG.error("PollInterval field is not a valid int");
				}
			}
			if (apiSchemeField.isShowing())
				config.setApiScheme(apiSchemeField.getText());
			if (apiBaseField.isShowing())
				config.setApiBase(apiBaseField.getText());
			if (apiPathField.isShowing())
				config.setApiPath(apiPathField.getText());
			if (apiKeyField.isShowing())
				config.setApiKey(apiKeyField.getText());
		}
		@Override
		public void removeUpdate(DocumentEvent e) {update();}
		
		@Override
		public void insertUpdate(DocumentEvent e) {update();}
		
		@Override
		public void changedUpdate(DocumentEvent e) {update();}
	};
	
	private DocumentListener numberValidator = new DocumentListener() {
		private void action() {
			try {
				Integer.parseInt(pollField.getText());
				pollErrorLabel.setVisible(false);
				pollField.setBorder(pollBorder);
			} catch (NumberFormatException e) {
				LOG.warn("Invalid input for poll field.", e);
				pollErrorLabel.setVisible(true);
				pollField.setBorder(pollErrorBorder);
			}
		}
		@Override
		public void removeUpdate(DocumentEvent e) {action();}
		@Override
		public void insertUpdate(DocumentEvent e) {action();}
		@Override
		public void changedUpdate(DocumentEvent e) {action();}
	};
}
