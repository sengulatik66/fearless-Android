package jp.co.soramitsu.wallet.impl.presentation.balance.nft.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.common.compose.component.B0
import jp.co.soramitsu.common.compose.component.B1
import jp.co.soramitsu.common.compose.component.BackgroundCornered
import jp.co.soramitsu.common.compose.component.CapsTitle
import jp.co.soramitsu.common.compose.component.FearlessCorneredShape
import jp.co.soramitsu.common.compose.component.GradientIcon
import jp.co.soramitsu.common.compose.component.H3
import jp.co.soramitsu.common.compose.component.H4
import jp.co.soramitsu.common.compose.component.MarginHorizontal
import jp.co.soramitsu.common.compose.component.MarginVertical
import jp.co.soramitsu.common.compose.models.Render
import jp.co.soramitsu.common.compose.models.ScreenLayout
import jp.co.soramitsu.common.compose.models.retrievePainter
import jp.co.soramitsu.common.compose.models.retrieveString
import jp.co.soramitsu.common.compose.theme.black50
import jp.co.soramitsu.common.compose.theme.customColors
import jp.co.soramitsu.common.compose.theme.warningOrange
import jp.co.soramitsu.common.compose.theme.white
import jp.co.soramitsu.common.compose.theme.white50
import jp.co.soramitsu.common.utils.castOrNull
import jp.co.soramitsu.common.utils.clickableSingle
import jp.co.soramitsu.common.compose.utils.PageScrollingCallback
import jp.co.soramitsu.common.compose.utils.nestedScrollConnectionForPageScrolling
import jp.co.soramitsu.wallet.impl.presentation.balance.nft.list.models.NFTCollectionsScreenModel
import jp.co.soramitsu.wallet.impl.presentation.balance.nft.list.models.NFTCollectionsScreenView

@Composable
fun NFTScreen(collectionsScreen: NFTCollectionsScreenModel) {
    val screenLayout = collectionsScreen.views.firstOrNull {
        it is NFTCollectionsScreenView.ItemModel
    }?.castOrNull<NFTCollectionsScreenView.ItemModel>()?.screenLayout
        ?: ScreenLayout.Grid

    Column {
        MarginVertical(margin = 8.dp)

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            NftSettingsBar(
                NftSettingsState(screenLayout, collectionsScreen.areFiltersApplied),
                appearanceSelected = collectionsScreen.onScreenLayoutChanged,
                filtersClicked = collectionsScreen.onFiltersIconClick
            )

            MarginHorizontal(margin = 21.dp)
        }

        MarginVertical(margin = 6.dp)

        NFTLayout(
            views = collectionsScreen.views,
            pageScrollingCallback = collectionsScreen.pageScrollingCallback
        )
    }
}

@Composable
private fun NFTLayout(
    views: SnapshotStateList<NFTCollectionsScreenView>,
    pageScrollingCallback: PageScrollingCallback
) {
    val lazyGridState = rememberLazyGridState()

    val nestedScrollConnection = remember(lazyGridState) {
        lazyGridState.nestedScrollConnectionForPageScrolling(pageScrollingCallback)
    }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.nestedScroll(nestedScrollConnection)
    ) {
        for (view in views) {
            when(view) {
                is NFTCollectionsScreenView.EmptyPlaceholder ->
                    NFTEmptyPlaceholder(view)

                is NFTCollectionsScreenView.LoadingIndication ->
                    NFTLoadingIndication(view)

                is NFTCollectionsScreenView.ItemModel ->
                    NFTCollectionItem(view)
            }
        }

        item(
            span = { GridItemSpan(2) }
        ) { MarginVertical(margin = 80.dp) }
    }
}

@Suppress("FunctionName")
private fun LazyGridScope.NFTEmptyPlaceholder(
    placeholderModel: NFTCollectionsScreenView.EmptyPlaceholder
) {
    item(
        span = {
            GridItemSpan(2)
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientIcon(
                    iconRes = placeholderModel.image.id,
                    color = warningOrange
                )

                H3(
                    text = placeholderModel.header.retrieveString()
                )

                B0(
                    text = placeholderModel.body.retrieveString(),
                    color = white50,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun LazyGridScope.NFTLoadingIndication(
    loadingIndication: NFTCollectionsScreenView.LoadingIndication
) {
    item(
        span = {
            GridItemSpan(2)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = white
            )
        }
    }
}

@Suppress("FunctionName")
private fun LazyGridScope.NFTCollectionItem(
    itemModel: NFTCollectionsScreenView.ItemModel
) {
    item(
        span = {
            if (itemModel.screenLayout === ScreenLayout.List){
                GridItemSpan(2)
            } else GridItemSpan(1)
        }
    ) {
        BackgroundCornered(
            modifier = Modifier.clickableSingle(onClick = itemModel.onItemClick)
        ) {
            itemModel.screenLayout.Render(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                val thumbnailSize = if (itemModel.screenLayout === ScreenLayout.Grid)
                    152.dp else 64.dp

                itemModel.thumbnail.Render(
                    shimmerModifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(thumbnailSize),
                    shimmerRadius = 11.dp
                ) { shimmerModifier, data ->
                    Box {
                        Image(
                            modifier = shimmerModifier.align(Alignment.Center),
                            painter = data.retrievePainter(),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop
                        )

                        if (
                            itemModel.screenLayout === ScreenLayout.Grid &&
                            itemModel is NFTCollectionsScreenView.ItemModel.WithQuantityDecorator
                        ) {
                            B1(
                                text = itemModel.quantity.retrieveString(),
                                color = white50,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(black50, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Column {
                    itemModel.chainName.Render(
                        shimmerModifier = Modifier
                            .fillMaxWidth(.45f)
                            .height(11.dp)
                    ) { _, data ->
                        CapsTitle(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .wrapContentHeight(),
                            text = data.retrieveString(),
                            color = white50,
                            maxLines = 1
                        )
                    }

                    itemModel.title.Render(
                        shimmerModifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(.9f)
                            .height(15.dp)
                    ) { _, data ->
                        H4(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .wrapContentHeight(),
                            text = data.retrieveString(),
                            color = white,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }

                    if (
                        itemModel.screenLayout === ScreenLayout.List &&
                        itemModel is NFTCollectionsScreenView.ItemModel.WithQuantityDecorator
                    ) {
                        B1(
                            text = itemModel.quantity.retrieveString(),
                            color = white50
                        )
                    }
                }
            }
        }
    }
}