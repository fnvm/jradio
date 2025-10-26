package io.github.fnvm.jradio.core.model;

import java.util.Objects;

public class RadioStation {
	private final String id;
	private String name;
	private String url;
	private String note;
	private boolean favorite;

	public RadioStation(String name, String url) {
		this.id = generateId();
		this.name = name;
		this.url = url;
		this.note = "No note";
		this.favorite = false;
	}

	private String generateId() {
		return "station_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String genre) {
		this.note = genre;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public String toString() {
		return "RadioStation [name=" + name + ", id=" + id + ", url=" + url + ", note=" + note + ", favorite="
				+ favorite + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(favorite, note, id, name, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RadioStation other = (RadioStation) obj;
		return favorite == other.favorite && Objects.equals(note, other.note) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(url, other.url);
	}

}
