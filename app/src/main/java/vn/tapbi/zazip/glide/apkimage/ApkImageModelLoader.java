/*
 * Copyright (C) 2014-2020 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vn.tapbi.zazip.glide.apkimage;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class ApkImageModelLoader implements ModelLoader<String, Drawable> {

  private Context context;

  public ApkImageModelLoader(Context context) {
    this.context = context;
  }

  @Nullable
  @Override
  public LoadData<Drawable> buildLoadData(String s, int width, int height, Options options) {
    return new LoadData<>(new ObjectKey(s), new ApkImageDataFetcher(context, s));
  }

  @Override
  public boolean handles(String s) {
    return s.substring(s.length() - 4, s.length()).toLowerCase().equals(".apk");
  }
}
