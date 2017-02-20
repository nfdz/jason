/*
 * (C) Copyright 2017 Jason (https://github.com/nfdz/jason).
 *
 * Licensed under the GNU General Public License, Version 3;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Contributors:
 *     nfdz
 */
package io.github.nfdz.jason.model;

import java.util.Collections;
import java.util.List;

/**
 * This POJO class contains all information that defines a snippet.
 */
public class Snippet {
	
	private final String mName;
	private final String mLanguage;
	private final String mCode;
	private final List<String> mTags;
	private final long mCreationTime; 
	
	/**
	 * Constructor. No field can be null.
	 * @param name
	 * @param language
	 * @param code
	 * @param tags
	 * @param creationTime
	 */
	public Snippet (String name, String language, String code, List<String> tags, long creationTime) {
		mName = name;
		mLanguage = language;
		mCode = code;
		mTags = Collections.unmodifiableList(tags);
		mCreationTime = creationTime;
	}

	public String getName() {
		return mName;
	}

	public String getLanguage() {
		return mLanguage;
	}
	
	public String getCode() {
		return mCode;
	}

	public List<String> getTags() {
		return mTags;
	}

	public long getCreationTime() {
		return mCreationTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (mCode.hashCode());
		result = prime * result + (int) (mCreationTime ^ (mCreationTime >>> 32));
		result = prime * result + (mLanguage.hashCode());
		result = prime * result + (mName.hashCode());
		result = prime * result + (mTags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		Snippet other = (Snippet) obj;
		if (!mCode.equals(other.mCode))
			return false;
		if (mCreationTime != other.mCreationTime)
			return false;
		if (!mLanguage.equals(other.mLanguage))
			return false;
		if (!mName.equals(other.mName))
			return false;
		if (!mTags.equals(other.mTags))
			return false;
		return true;
	}
	
}
