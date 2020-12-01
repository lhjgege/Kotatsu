package org.koitharu.kotatsu.reader.ui.reversed

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.android.ext.android.inject
import org.koitharu.kotatsu.core.prefs.AppSettings
import org.koitharu.kotatsu.databinding.FragmentReaderStandardBinding
import org.koitharu.kotatsu.reader.ui.ReaderState
import org.koitharu.kotatsu.reader.ui.base.AbstractReader
import org.koitharu.kotatsu.reader.ui.base.BaseReaderAdapter
import org.koitharu.kotatsu.reader.ui.base.ReaderPage
import org.koitharu.kotatsu.reader.ui.standard.PageAnimTransformer
import org.koitharu.kotatsu.reader.ui.standard.PagerPaginationListener
import org.koitharu.kotatsu.utils.ext.doOnPageChanged
import org.koitharu.kotatsu.utils.ext.swapAdapter
import org.koitharu.kotatsu.utils.ext.withArgs

class ReversedReaderFragment : AbstractReader<FragmentReaderStandardBinding>(),
	SharedPreferences.OnSharedPreferenceChangeListener {

	private var paginationListener: PagerPaginationListener? = null
	private val settings by inject<AppSettings>()

	override fun onInflateView(
		inflater: LayoutInflater,
		container: ViewGroup?
	) = FragmentReaderStandardBinding.inflate(inflater, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		paginationListener = PagerPaginationListener(readerAdapter!!, 2, this)
		with(binding.pager) {
			adapter = readerAdapter
			if (settings.readerAnimation) {
				setPageTransformer(ReversedPageAnimTransformer())
			}
			offscreenPageLimit = 2
			registerOnPageChangeCallback(paginationListener!!)
			doOnPageChanged {
				notifyPageChanged(reversed(it))
			}
		}
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		settings.subscribe(this)
	}

	override fun onDetach() {
		settings.unsubscribe(this)
		super.onDetach()
	}

	override fun onDestroyView() {
		paginationListener = null
		super.onDestroyView()
	}

	override fun onCreateAdapter(dataSet: List<ReaderPage>): BaseReaderAdapter {
		return ReversedPagesAdapter(dataSet, loader)
	}

	override fun recreateAdapter() {
		super.recreateAdapter()
		binding.pager.swapAdapter(readerAdapter)
	}

	override fun getCurrentItem() = reversed(binding.pager.currentItem)

	override fun setCurrentItem(position: Int, isSmooth: Boolean) {
		binding.pager.setCurrentItem(reversed(position), isSmooth)
	}

	override fun getCurrentPageScroll() = 0

	override fun restorePageScroll(position: Int, scroll: Int) = Unit

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
		when (key) {
			AppSettings.KEY_READER_ANIMATION -> {
				if (settings.readerAnimation) {
					binding.pager.setPageTransformer(PageAnimTransformer())
				} else {
					binding.pager.setPageTransformer(null)
				}
			}
		}
	}

	override fun getLastPage() = pages.firstOrNull()

	override fun getFirstPage() = pages.lastOrNull()

	private fun reversed(position: Int) = (itemsCount - position - 1).coerceAtLeast(0)

	companion object {

		fun newInstance(state: ReaderState) = ReversedReaderFragment().withArgs(1) {
			putParcelable(ARG_STATE, state)
		}
	}
}