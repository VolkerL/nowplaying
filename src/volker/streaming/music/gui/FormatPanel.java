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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import volker.streaming.music.Config;
import volker.streaming.music.Formatter;

public class FormatPanel extends JPanel {

	private static final long serialVersionUID = 438719686207902903L;
	
	private static final Log LOG = LogFactory.getLog(FormatPanel.class);

	private JButton formatInfoButton;
	private JLabel formatLabel;
	private JLabel templateLabel;
	private JTextArea formatArea;
	private Highlighter formatLighter;
	private Highlighter.HighlightPainter formatPainter;
	
	private final Formatter formatter;
	private final List<String> properTags;
	private JScrollPane tagScrollPane;
	private JList<String> tagList;
	private JLabel tagLabel;
	
	private final Object previewObject;
	private JLabel previewLabel;
	private JTextField previewField;
	
	private JLabel nullMessageLabel;
	private JTextField nullMessageField;
	
	private JSeparator hline;
	private JFileChooser fileChooser;
	private JLabel fileLabel;
	private JTextField fileField;
	private JButton fileButton;
	
	private JFrame parent;
	private Config config;
	
	public FormatPanel(JFrame parent, Config config, Formatter formatter, Object previewObject) {
		this.parent = parent;
		this.config = config;
		this.formatter = formatter;
		this.previewObject = previewObject;
		properTags = new ArrayList<String>(formatter.getTags(previewObject.getClass()));
		initComponents();
		initLayout();
	}
	
	private void initComponents() {
		formatLabel = new JLabel("How should your track info be displayed:");
		formatArea = new JTextArea(config.getFormat());
		formatLighter = new DefaultHighlighter();
		// TODO allow configuration of this color
		formatPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(200, 200, 255));
		formatArea.setHighlighter(formatLighter);
		formatArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {formatUpdated();}
			@Override
			public void insertUpdate(DocumentEvent e) {formatUpdated();}
			@Override
			public void changedUpdate(DocumentEvent e) {formatUpdated();}
		});
		
		ImageIcon infoIcon = null;
		try {
			InputStream is = getClass().getResourceAsStream("info.png");
			if (is == null) {
				LOG.error("Couldn't find the info image.");
			} else {
				infoIcon = new ImageIcon(ImageIO.read(is));
				is.close();
			}
		} catch (IOException e1) {
			LOG.error("Couldn't find the info image.", e1);
		}
		if (infoIcon == null) {
			formatInfoButton = new JButton("?");
		} else {
			formatInfoButton = new JButton(infoIcon);
			formatInfoButton.setBorder(BorderFactory.createEmptyBorder());
		}
		
		
		formatInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { showFormatHelp();}
		});
		
		templateLabel = new JLabel("Your template:");
		
		tagLabel = new JLabel("Available tags:");
		tagList = new JList<String>(new AbstractListModel<String>() {
			private static final long serialVersionUID = -8886588605378873151L;
			@Override
			public int getSize() {
				return properTags.size();
			}

			@Override
			public String getElementAt(int index) {
				return properTags.get(index);
			}
		});
		tagScrollPane = new JScrollPane(tagList);
		
		previewLabel = new JLabel("Preview:");
		previewField = new JTextField();
		previewField.setEditable(false);
		previewField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		previewField.setBackground(new Color(255, 255, 150));
		
		formatUpdated();
		highlightTags();
		
		nullMessageLabel = new JLabel("Message to display when no song is found:");
		nullMessageField = new JTextField(config.getNoTrackMessage() == null? "" : config.getNoTrackMessage());
		nullMessageField.getDocument().addDocumentListener(new DocumentListener() {
			public void action() {
				config.setFormat(nullMessageField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {action();}
			@Override
			public void insertUpdate(DocumentEvent e) {action();}
			@Override
			public void changedUpdate(DocumentEvent e) {action();}
		});
		
		hline = new JSeparator(SwingConstants.HORIZONTAL);
		fileLabel = new JLabel("Location of text file:");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileField = new JTextField(15);
		if (config.getOutputFile() != null) {
			fileField.setText(config.getOutputFile().getAbsolutePath());
		}
		fileField.getDocument().addDocumentListener(new DocumentListener() {
			public void action() {
				config.setOutputFile(new File(fileField.getText()));
			}
			@Override
			public void removeUpdate(DocumentEvent e) {action();}
			@Override
			public void insertUpdate(DocumentEvent e) {action();}
			@Override
			public void changedUpdate(DocumentEvent e) {action();}
		});
		fileButton = new JButton("Browse");
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {browseFile();}
		});
	}
	
	private void initLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(formatLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(formatInfoButton)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(previewLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(previewField)
				)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(templateLabel)
							.addComponent(formatArea)
					)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(tagLabel)
						.addComponent(tagScrollPane)
					)
				)
				.addComponent(nullMessageLabel)
				.addComponent(nullMessageField)
				.addComponent(hline)
				.addComponent(fileLabel)
				.addGroup(layout.createSequentialGroup()
					.addComponent(fileField)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(fileButton)
				)
			)
			.addContainerGap()
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(formatLabel)
				.addComponent(formatInfoButton)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(previewLabel)
				.addComponent(previewField)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(templateLabel)
					.addComponent(formatArea)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(tagLabel)
					.addComponent(tagScrollPane)
				)
			)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(nullMessageLabel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(nullMessageField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(hline, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(fileLabel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(fileField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(fileButton)
			)
			.addContainerGap()
		);
	}
	
	// executed on EDT (called in documentlistener)
	private void formatUpdated() {
		previewField.setText(formatter.format(previewObject, formatArea.getText()));
		highlightTags();
	}
	
	private void showFormatHelp() {
		final StringBuilder builder = new StringBuilder();
		int width = parent.getBounds().width;
		builder.append("<html><body><div style=\"width:").append(width).append("px;\">")
		.append("<p>")
		.append("Here you can specify how your current playing track should be displayed.")
		.append("</p><p>")
		.append("The idea is that this application will retrieve the track you are currently playing.")
		.append("This information will be written to a text file, so you can include it in your stream.")
		.append("To determine how the track's information should be displayed,")
		.append("you have to provide a template in which the application will insert the actual track information.")
		.append("</p><p>")
		.append("Let's look at an example template:")
		.append("<pre>")
		.append("I'm listening to {name}. Which is performed by {artist}.")
		.append("</pre>")
		.append("The parts in curly braces are tags. The application will replace them with the actual track information.")
		.append("So if I'm listening to Beyonce's song called Halo, the text file written by this application will contain:")
		.append("<pre>")
		.append("I'm listening to Halo. Which is performed by Beyonce.")
		.append("</pre>")
		.append("</p>")
		.append("<p>")
		.append("To find out what tags are available, use the list on the right of the text area.")
		.append("</p>")
		.append("Finally, you can see what your template would look like in the preview field.")
		.append("</div></body></html>");
		JOptionPane.showMessageDialog(formatArea, builder.toString(), "Displaying track info", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void browseFile() {
		int result = fileChooser.showOpenDialog(this);
		if (JFileChooser.APPROVE_OPTION == result) {
			File output = fileChooser.getSelectedFile();
			config.setOutputFile(output);
			fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void highlightTags() {
		formatLighter.removeAllHighlights();
		String template = formatArea.getText();
		Pattern regex = Pattern.compile("\\{[^{}]*\\}");
		Matcher matcher = regex.matcher(template);
		int offset = 0;
		while (matcher.find(offset)) {
			offset = matcher.end();
			String tag = matcher.group();
			if (properTags.contains(tag)) {
				try {
					formatLighter.addHighlight(matcher.start(), matcher.end(), formatPainter);
				} catch (BadLocationException e) {
					LOG.error("Failed to add highlight for tag " + tag, e);
				}
			}
		}
		
	}
}
