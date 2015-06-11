package sg.gov.msf.bbss.apputils.security;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bandaray on 11/12/2014.
 */
public class ScuredSharedPreferences implements SharedPreferences {

	protected SharedPreferences delegate;
	protected Context context;
	protected Cryptographer security;

	public ScuredSharedPreferences(Context context, SharedPreferences delegate) {
		this.delegate = delegate;
		this.context = context;
		this.security = new Cryptographer(context);
	}

	//-----------------------------------------------------------------------------------------------------------

	public class SecureSharedPrefEditor implements SharedPreferences.Editor {
		protected SharedPreferences.Editor delegate;

		public SecureSharedPrefEditor() {
			this.delegate = ScuredSharedPreferences.this.delegate.edit();                    
		}

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		@Override
		public Editor putBoolean(String key, boolean value) {
			delegate.putString(key, security.encrypt(Boolean.toString(value)));
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			delegate.putString(key, security.encrypt(Float.toString(value)));
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			delegate.putString(key, security.encrypt(Integer.toString(value)));
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			delegate.putString(key, security.encrypt(Long.toString(value)));
			return this;
		}

		@Override
		public Editor putString(String key, String value) {
			delegate.putString(key, security.encrypt(value));
			return this;
		}

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		@Override
		public void apply() {
			//			delegate.apply();
			//			Require API level9
		}

		@Override
		public Editor clear() {
			delegate.clear();
			return this;
		}

		@Override
		public boolean commit() {
			return delegate.commit();
		}

		@Override
		public Editor remove(String s) {
			delegate.remove(s);
			return this;
		}

		@Override
		public android.content.SharedPreferences.Editor putStringSet(String key, Set<String> values) {
			return null;
		}
	}

	//-----------------------------------------------------------------------------------------------------------

	public Editor edit() {
		return new SecureSharedPrefEditor();
	}

	@Override
	public boolean contains(String s) {
		return delegate.contains(s);
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
		delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
		delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
	}

	//-----------------------------------------------------------------------------------------------------------

	@Override
	public Set<String> getStringSet(String key, Set<String> defValues) {
		return null;
	}

	@Override
	public Map<String, ?> getAll() {
		throw new UnsupportedOperationException(); // left as an exercise to the reader
	}

	//-----------------------------------------------------------------------------------------------------------

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		final String v = delegate.getString(key, null);
		return v!=null ? Boolean.parseBoolean(security.decrypt(v)) : defValue;
	}

	@Override
	public float getFloat(String key, float defValue) {
		final String v = delegate.getString(key, null);
		return v!=null ? Float.parseFloat(security.decrypt(v)) : defValue;
	}

	@Override
	public int getInt(String key, int defValue) {
		final String v = delegate.getString(key, null);
		return v!=null ? Integer.parseInt(security.decrypt(v)) : defValue;
	}

	@Override
	public long getLong(String key, long defValue) {
		final String v = delegate.getString(key, null);
		return v!=null ? Long.parseLong(security.decrypt(v)) : defValue;
	}

	@Override
	public String getString(String key, String defValue) {
		final String v = delegate.getString(key, null);
		return v != null ? security.decrypt(v) : defValue;
	}
}
