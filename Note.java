
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Note.java
 * This class represents a fully described
 * note event. Secondary descriptors such as
 * beat in measure and tick in beat can be stored.
 *  
 * @author Evan X. Merz
 */
public class Note implements Serializable, Cloneable, Comparable<Note>
{
	// unique id for this note
	public UUID guid;

	// Map of guid -> weight that describes the link to another note
	// link strength = 1 / (# of beats between onsets)
	// this will map to the next and previous pitches
	// notes are not linked after a weight threshold (say 0.1)
	//private HashMap<UUID, Double> edges;
	// NOTE: It doesn't make sense to have the edges in this class.
	// Edges should be in a class that describes a piece
	
	public int pitch;
	public String pitchName;
	
	// this is the duration in the timing of the original file
	public int startTickSource;
	public int endTickSource;
	public int durationSource;
	
	public double durationInBeats;
	public String durationName;
	public int beatInMeasure; // the beat during which this note occurs in the measure
	
	public int beatInPiece; // the beat during which this note occurs in the piece
	
	// TODO: store the velocity, should be in data2 of the midi event
	public int velocity;
	
	// 12ths of a beat captures everything down to triplet 16ths
	// they will provide a basis for comparison
	public int positionInMeasureInTwelfths;
	public int positionInBeatInTwelfths;
	public int durationInTwelfths;
	
	// TODO: is this a grace note? how to determine this?
	//public boolean GraceNote;
	
	// information about the source file
	public int ticksPerBeatSource = -1;
	public int beatsPerMeasureSource = 4;

	public Note(int ticksPerBeatFromSource, int beatsPerMeasureFromSource)
	{
		// get a unique id
		guid = UUID.randomUUID();
		
		// set the rhythm parameters
		setRhythmParameters(ticksPerBeatFromSource, beatsPerMeasureFromSource);
	}
	
	@Override
	public Note clone()
	{
		Note copy = new Note(ticksPerBeatSource, beatsPerMeasureSource);
		copy.setPitch(this.pitch);
		copy.setDuration(this.startTickSource, this.endTickSource);
		return copy;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if( o == null ) return false;
		
		if(o instanceof Note)
		{
			Note b = (Note)o;
			
			if( this.pitch != b.pitch ) return false;
			if( this.durationSource != b.durationSource ) return false;
			if( this.startTickSource != b.startTickSource ) return false;
			if( this.positionInMeasureInTwelfths != b.positionInMeasureInTwelfths ) return false;
		
			return true;
		}
		
		return false;
	}
	
	/**
	 * setRhythmParameters. This method sets two properties of
	 * the source file that are required for duration-related calculations:
	 * ticksPerBeatSource, and beatsPerMeasureSource.
	 * 
	 * @param ticksPerBeatFromSource the number of ticks in a beat in the midi file
	 * @param beatsPerMeasureFromSource the number of beats in a measure in the midi file
	 */
	public void setRhythmParameters(int ticksPerBeatFromSource, int beatsPerMeasureFromSource)
	{
		this.ticksPerBeatSource = ticksPerBeatFromSource;
		this.beatsPerMeasureSource = beatsPerMeasureFromSource;
	}
	
	/**
	 * setPitch. Sets all pitch related variables on this object.
	 * 
	 * @param newPitch the MIDI pitch from the source file
	 */
	public void setPitch(int newPitch)
	{
		this.pitch = newPitch;
		
		if( this.pitch > -1 && this.pitch < 128 )
			this.pitchName = MidiUtil.MIDI_PITCH_NAMES[newPitch % MidiUtil.MIDI_PITCH_NAMES.length];
		else
			this.pitchName = "R";
	}
	
	public void setDuration(int startTickSource, int endTickSource)
	{
		// 1. store the duration in the timing of the original file
		this.startTickSource = startTickSource;
		this.endTickSource = endTickSource;
		this.durationSource = endTickSource - startTickSource;

		// 2. store the perceived properties of the duration
		this.durationInBeats = (double)durationSource / (double)ticksPerBeatSource;
		this.durationName = getDurationName(durationInBeats);
		
		// 3. store the duration-related values in 12ths, which will provide a simple basis for comparison
		// durationInTwelfths = durationInTicks / (ticksPerBeatSource / 12)
		this.durationInTwelfths = durationSource / (ticksPerBeatSource / 12);
		// posIn12ths = (startTick % (ticksPerBeat * beatsPerMeasure)) / (ticksPerBeatSource / 12)
		this.positionInMeasureInTwelfths = (startTickSource % (ticksPerBeatSource * beatsPerMeasureSource)) / (ticksPerBeatSource / 12);
		this.positionInBeatInTwelfths = (startTickSource % ticksPerBeatSource) / (ticksPerBeatSource / 12);;
		
		// 4. store the position in the measure and piece
		this.beatInMeasure = positionInMeasureInTwelfths / 12; // the beat during which this note occurs
		this.beatInPiece = startTickSource / ticksPerBeatSource;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getName() + "[" + pitchName + ", " + durationName + " note, starting at " + startTickSource + "]";
	}
	
	/**
	 * toCommaDelimitedString. This should output to ARFF for 
	 * use in Weka. 
	 * 
	 * http://www.cs.waikato.ac.nz/ml/weka/arff.html
	 * http://weka.wikispaces.com/ARFF
	 * 
	 * @return a comma delimited string
	 */
	public String toCommaDelimitedString()
	{
		return pitch + ", " + durationInTwelfths + ", " + positionInMeasureInTwelfths;
	}
	
	/**
	 * print. Use reflection to print the details of this Note_V object.
	 */
	public void print()
	{
		Field[] fields = this.getClass().getFields();
		try
		{
			for( Field f : fields )
			{
				System.out.println(f.getName() + " = " + f.get(this));
			}
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * getDurationName. Return a best guess at the duration
	 * name, based on a double representing the duration in
	 * beats.
	 * 
	 * @param durationInBeats
	 * @return
	 */
	private String getDurationName(double durationInBeats)
	{
		if( durationInBeats >= 0.99 && durationInBeats <= 1.01 ) return "quarter";
		else if( durationInBeats >= 1.99 && durationInBeats <= 2.01 ) return "half";
		else if( durationInBeats >= 2.99 && durationInBeats <= 3.01 ) return "dotted half";
		else if( durationInBeats >= 3.99 && durationInBeats <= 4.01 ) return "whole";
		
		else if( durationInBeats >= 0.24 && durationInBeats <= 0.26 ) return "sixteenth";
		else if( durationInBeats >= 0.49 && durationInBeats <= 0.51 ) return "eighth";
		else if( durationInBeats >= 0.74 && durationInBeats <= 0.76 ) return "dotted eighth";
		else if( durationInBeats >= 1.24 && durationInBeats <= 1.26 ) return "quarter tied to sixteenth";
		else if( durationInBeats >= 1.49 && durationInBeats <= 1.51 ) return "dotted quarter";
		else if( durationInBeats >= 1.74 && durationInBeats <= 1.76 ) return "doubly dotted quarter";
		
		else if( durationInBeats >= 0.15 && durationInBeats <= 0.18 ) return "triplet sixteenth";
		else if( durationInBeats >= 0.31 && durationInBeats <= 0.35 ) return "triplet eighth";
		else if( durationInBeats >= 0.64 && durationInBeats <= 0.68 ) return "triplet quarter";
		else if( durationInBeats >= 1.31 && durationInBeats <= 1.35 ) return "triplet half";
		
		return "UNKNOWN";
	}

	/**
	 * compareTo. The Comparable interface. Implemented so
	 * that a collection of Note objects can be sorted.
	 */
	@Override
	public int compareTo(Note b)
	{
		if( this.startTickSource < b.startTickSource) return -1;
		else if( this.startTickSource > b.startTickSource ) return 1;
		return 0;
	}
}

