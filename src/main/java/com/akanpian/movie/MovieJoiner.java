package com.akanpian.movie;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class MovieJoiner {

	public static void videoMerge(String[] srcVideoPath, String dstVideoPath) throws IOException {

		List<Movie> inMovies = new ArrayList<Movie>();
		for (String videoUri : srcVideoPath) {
			inMovies.add(MovieCreator.build(videoUri));
			
		}

		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();

		for (Movie m : inMovies) {
			for (Track t : m.getTracks()) {
				if (t.getHandler().equals("soun")) {
					audioTracks.add(t);
				}
				if (t.getHandler().equals("vide")) {
					videoTracks.add(t);
				}
			}
		}

		Movie result = new Movie();

		if (audioTracks.size() > 0) {
			result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
		}
		if (videoTracks.size() > 0) {
			result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
		}

		Container out = new DefaultMp4Builder().build(result);

		FileChannel fc = new RandomAccessFile(String.format(dstVideoPath), "rw").getChannel();
		out.writeContainer(fc);
		fc.close();
	}
	public static void main(String[] args) throws IOException {
		String[] ins = new String[] { "F:/my/software/windows/study/系列/Japan Star/[上原亚衣]眼镜×女孩『上原亚衣』的校园生活 1.mp4", "F:/my/software/windows/study/系列/Japan Star/[上原亚衣]眼镜×女孩『上原亚衣』的校园生活 2.mp4"};
		videoMerge(ins, "c:/a.mp4");
	}

}
