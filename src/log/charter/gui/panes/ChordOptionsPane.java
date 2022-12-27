package log.charter.gui.panes;

import static log.charter.gui.components.TextInputWithValidation.ValueValidator.createIntValidator;

import javax.swing.JTextField;

import log.charter.data.ChartData;
import log.charter.data.config.Localization.Label;
import log.charter.data.managers.selection.ChordOrNote;
import log.charter.data.undoSystem.UndoSystem;
import log.charter.gui.CharterFrame;
import log.charter.gui.components.ChordTemplateEditor;
import log.charter.song.ChordTemplate;
import log.charter.song.enums.HOPO;
import log.charter.song.enums.Harmonic;
import log.charter.song.enums.Mute;
import log.charter.song.notes.Chord;
import log.charter.song.notes.Note;
import log.charter.util.CollectionUtils.ArrayList2;

public class ChordOptionsPane extends ChordTemplateEditor {
	private static final long serialVersionUID = 1L;

	private static PaneSizes getSizes() {
		final PaneSizes sizes = new PaneSizes();
		sizes.labelWidth = 80;
		sizes.width = 350;
		sizes.rowHeight = 20;

		return sizes;
	}

	private static ChordTemplate prepareTemplateFromData(final ChartData data, final ChordOrNote chordOrNote) {
		return !chordOrNote.isChord() || chordOrNote.chord.chordId == -1 ? new ChordTemplate()
				: new ChordTemplate(data.getCurrentArrangement().chordTemplates.get(chordOrNote.chord.chordId));
	}

	private final UndoSystem undoSystem;

	private final ArrayList2<ChordOrNote> chordsAndNotes;

	private Mute mute;
	private HOPO hopo = HOPO.NONE;
	private Harmonic harmonic = Harmonic.NONE;
	private boolean accent;
	private boolean linkNext;
	private Integer slideTo;
	private boolean unpitchedSlide;

	public ChordOptionsPane(final ChartData data, final CharterFrame frame, final UndoSystem undoSystem,
			final ArrayList2<ChordOrNote> notes) {
		super(data, frame, Label.CHORD_OPTIONS_PANE, 18 + data.getCurrentArrangement().tuning.strings, getSizes(),
				prepareTemplateFromData(data, notes.get(0)));
		this.undoSystem = undoSystem;

		chordsAndNotes = notes;

		final ChordOrNote chordOrNote = notes.get(0);
		if (chordOrNote.note != null) {
			getNoteValues(chordOrNote.note);
		} else {
			final Chord chord = chordOrNote.chord;
			final ChordTemplate chordTemplate = data.getCurrentArrangement().chordTemplates.get(chord.chordId);
			getChordValues(chord, chordTemplate);
		}

		addInputs(data.getCurrentArrangement().tuning.strings);
	}

	private void getNoteValues(final Note note) {
		mute = note.mute;
		hopo = note.hopo;
		harmonic = note.harmonic;
		accent = note.accent;
		linkNext = note.linkNext;
		slideTo = note.slideTo;
		unpitchedSlide = note.unpitchedSlide;
	}

	private void getChordValues(final Chord chord, final ChordTemplate chordTemplate) {
		mute = chord.mute;
		hopo = chord.hopo;
		harmonic = chord.harmonic;
		accent = chord.accent;
		linkNext = chord.linkNext;
		slideTo = chord.slideTo;
		unpitchedSlide = chord.unpitchedSlide;
	}

	private void addInputs(final int strings) {
		addChordNameSuggestionButton(100, 0);
		addChordNameInput(100, 1);

		addChordTemplateEditor(3);

		int row = 4 + data.currentStrings();
		final int radioButtonWidth = 65;

		row++;
		final int muteLabelY = getY(row++);
		addLabelExact(muteLabelY, 20, Label.MUTE);
		final int muteRadioY = getY(row++) - 3;
		addConfigRadioButtonsExact(muteRadioY, 30, radioButtonWidth, mute.ordinal(), i -> mute = Mute.values()[i], //
				Label.MUTE_STRING, Label.MUTE_PALM, Label.MUTE_NONE);

		final int hopoLabelY = getY(row++) + 3;
		addLabelExact(hopoLabelY, 20, Label.HOPO);
		final int hopoRadioY = getY(row++);
		addConfigRadioButtonsExact(hopoRadioY, 30, radioButtonWidth, hopo.ordinal(), i -> hopo = HOPO.values()[i], //
				Label.HOPO_HAMMER_ON, Label.HOPO_PULL_OFF, Label.HOPO_TAP, Label.HOPO_NONE);

		final int harmonicLabelY = getY(row++) + 6;
		addLabelExact(harmonicLabelY, 20, Label.HARMONIC);
		final int harmonicRadioY = getY(row++) + 3;
		addConfigRadioButtonsExact(harmonicRadioY, 30, radioButtonWidth, harmonic.ordinal(),
				i -> harmonic = Harmonic.values()[i], //
				Label.HARMONIC_NORMAL, Label.HARMONIC_PINCH, Label.HARMONIC_NONE);

		row++;
		addConfigCheckbox(row, 20, 45, Label.ACCENT, accent, val -> accent = val);
		addConfigCheckbox(row++, 110, 0, Label.LINK_NEXT, linkNext, val -> linkNext = val);

		row++;
		addIntegerConfigValue(row, 20, 45, Label.SLIDE_PANE_FRET, slideTo, 30, createIntValidator(0, 28, true),
				val -> slideTo = val, false);
		((JTextField) components.getLast()).setHorizontalAlignment(JTextField.CENTER);
		addConfigCheckbox(row, 120, unpitchedSlide, val -> unpitchedSlide = val);
		addLabel(row++, 140, Label.SLIDE_PANE_UNPITCHED);

		row++;
		row++;
		addDefaultFinish(row, this::onSave);
	}

	private void setChordValues(final Chord chord) {
		chord.mute = mute;
		chord.hopo = hopo;
		chord.harmonic = harmonic;
		chord.accent = accent;
		chord.linkNext = linkNext;
		chord.slideTo = slideTo;
		chord.unpitchedSlide = unpitchedSlide;
	}

	private void changeNoteToChord(final ChordOrNote chordOrNote, final int chordId) {
		final Chord chord = new Chord(chordOrNote.position, chordId);
		setChordValues(chord);
		chordOrNote.note = null;
		chordOrNote.chord = chord;
	}

	private void onSave() {
		dispose();
		undoSystem.addUndo();

		if (chordTemplate.frets.isEmpty()) {
			data.getCurrentArrangementLevel().chordsAndNotes.removeAll(chordsAndNotes);
			return;
		}

		final int chordId = getSavedTemplateId();
		for (final ChordOrNote chordOrNote : chordsAndNotes) {
			if (chordOrNote.isChord()) {
				chordOrNote.chord.chordId = chordId;
				setChordValues(chordOrNote.chord);
			} else {
				changeNoteToChord(chordOrNote, chordId);
			}
		}
	}

}
