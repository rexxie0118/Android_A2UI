package com.example.androiduirenderer.core

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.androiduirenderer.model.PageConfig
import com.example.androiduirenderer.model.WidgetConfig

class NavigationManager(
    private val uiRenderer: UIRenderer,
    private val container: ViewGroup,
    private val actionHandler: (String, WidgetConfig) -> Unit = { _, _ -> }
) {
    private val pageStack = mutableListOf<PageConfig>()
    private val pagesById = mutableMapOf<String, PageConfig>()
    private var currentPageId: String? = null

    fun loadPages(pages: List<PageConfig>) {
        pagesById.clear()
        pages.forEach { page ->
            pagesById[page.id] = page
        }
        if (pages.isNotEmpty()) {
            navigateToPage(pages.first().id)
        }
    }

    fun navigateToPage(pageId: String) {
        val page = pagesById[pageId] ?: throw IllegalArgumentException("Page $pageId not found")
        if (currentPageId != null) {
            pageStack.add(pagesById[currentPageId]!!)
        }
        currentPageId = pageId
        renderPage(page)
    }

    fun goBack(): Boolean {
        if (pageStack.isNotEmpty()) {
            val previousPage = pageStack.removeAt(pageStack.size - 1)
            currentPageId = previousPage.id
            renderPage(previousPage)
            return true
        }
        return false
    }

    fun goNext(): Boolean {
        val currentPage = currentPageId?.let { pagesById[it] } ?: return false
        val nextPageId = currentPage.nextPageId ?: return false
        navigateToPage(nextPageId)
        return true
    }

    fun goPrevious(): Boolean {
        val currentPage = currentPageId?.let { pagesById[it] } ?: return false
        val previousPageId = currentPage.previousPageId ?: return false
        navigateToPage(previousPageId)
        return true
    }

    fun getCurrentPage(): PageConfig? {
        return currentPageId?.let { pagesById[it] }
    }

    private fun renderPage(page: PageConfig) {
        Log.d("NavigationManager", "Rendering page ${page.id}")
        uiRenderer.renderPage(page.widgets, container)
        setupActions(page.widgets)
    }

    private fun setupActions(widgets: List<WidgetConfig>) {
        widgets.forEach { widget ->
            widget.id?.let { id ->
                 val view = container.findViewWithTag<View>(id)
                if (view != null) {
                    Log.d("NavigationManager", "Found view with tag $id, action=${widget.action}")
                     widget.action?.let { action ->
                         Log.d("NavigationManager", "Setting click listener for $id, clickable=${view.isClickable}, hasListener=${view.hasOnClickListeners()}")
                         view.setOnClickListener {
                             Log.d("NavigationManager", "Click on $id, action=$action")
                             actionHandler(action, widget)
                         }
                         Log.d("NavigationManager", "After set, hasListener=${view.hasOnClickListeners()}")
                     }
                } else {
                    Log.d("NavigationManager", "View not found for tag $id")
                }
            }
            widget.children?.let { setupActions(it) }
        }
    }
}