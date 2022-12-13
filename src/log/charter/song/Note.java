package log.charter.song;

import static log.charter.util.Utils.mapInteger;

import log.charter.data.managers.SelectionManager.Selectable;
import log.charter.io.rs.xml.song.ArrangementNote;
import log.charter.util.CollectionUtils.ArrayList2;

public class Note extends Selectable {
	public int string;
	public int fret;
	public int sustain;
	public Integer vibrato;
	public boolean accent;
	public boolean mute;
	public boolean palmMute;
	public boolean pluck;
	public boolean hopo;
	public boolean hammerOn;
	public boolean pullOff;
	public boolean slap;
	public Integer slideTo;
	public Integer slideUnpitchTo;
	public Integer bend;
	public boolean tap;
	public boolean harmonic;
	public boolean harmonicPinch;
	public ArrayList2<BendValue> bendValues;
	public boolean crazy;

	public Note(final int pos, final int string, final int fret) {
		super(pos);
		this.string = string;
		this.fret = fret;
	}

	public Note(final ArrangementNote arrangementNote) {
		super(arrangementNote.time);
		string = arrangementNote.string;
		fret = arrangementNote.fret;
		sustain = arrangementNote.sustain == null ? 0 : arrangementNote.sustain;
		vibrato = arrangementNote.vibrato;
		accent = mapInteger(arrangementNote.accent);
		mute = mapInteger(arrangementNote.mute);
		palmMute = mapInteger(arrangementNote.palmMute);
		pluck = mapInteger(arrangementNote.pluck);
		hammerOn = mapInteger(arrangementNote.hammerOn);
		pullOff = mapInteger(arrangementNote.pullOff);
		slap = mapInteger(arrangementNote.slap);
		slideTo = arrangementNote.slideTo;
		slideUnpitchTo = arrangementNote.slideUnpitchTo;
		bend = arrangementNote.bend;
		tap = mapInteger(arrangementNote.tap);
		harmonic = mapInteger(arrangementNote.harmonic);
		harmonicPinch = mapInteger(arrangementNote.harmonicPinch);
		bendValues = arrangementNote.bendValues == null ? new ArrayList2<>()
				: arrangementNote.bendValues.list.map(BendValue::new);
	}

	@Override
	public String getSignature() {
		return position + "-" + string;
	}

}
