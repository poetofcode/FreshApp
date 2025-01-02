package presentation.factories

import data.repository.FavoriteRepository
import data.repository.FeedRepository
import data.repository.ProfileRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.authScreen.AuthViewModel
import presentation.screens.bookmarkListScreen.BookmarkListViewModel
import presentation.screens.bookmarkTabScreen.BookmarkTabViewModel
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.notificationsScreen.NotificationsViewModel
import presentation.screens.postDetailsScreen.PostDetailsViewModel
import presentation.screens.postListScreen.PostListViewModel
import presentation.screens.profileScreen.ProfileViewModel
import presentation.screens.profileTabScreen.ProfileTabViewModel
import presentation.screens.regScreen.RegViewModel


class HomeTabViewModelFactory() : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

class BookmarkTabViewModelFactory() : ViewModelFactory<BookmarkTabViewModel> {
    override fun createViewModel(): BookmarkTabViewModel {
        return BookmarkTabViewModel()
    }

    override val vmTypeName: String
        get() = BookmarkTabViewModel::class.java.typeName

}

class PostListViewModelFactory(
    val feedRepository: FeedRepository,
    val favoriteRepository: FavoriteRepository,
) : ViewModelFactory<PostListViewModel> {
    override fun createViewModel(): PostListViewModel {
        return PostListViewModel(
            feedRepository = feedRepository,
            favoriteRepository = favoriteRepository,
        )
    }

    override val vmTypeName: String
        get() = PostListViewModel::class.java.typeName

}

class BookmarkListViewModelFactory(
    val favoriteRepository: FavoriteRepository,
) : ViewModelFactory<BookmarkListViewModel> {
    override fun createViewModel(): BookmarkListViewModel {
        return BookmarkListViewModel(
            favoriteRepository = favoriteRepository,
        )
    }

    override val vmTypeName: String
        get() = PostListViewModel::class.java.typeName

}

class PostDetailsViewModelFactory() : ViewModelFactory<PostDetailsViewModel> {
    override fun createViewModel(): PostDetailsViewModel {
        return PostDetailsViewModel()
    }

    override val vmTypeName: String
        get() = PostDetailsViewModel::class.java.typeName

}

class ProfileTabViewModelFactory
    : ViewModelFactory<ProfileTabViewModel> {
    override fun createViewModel(): ProfileTabViewModel {
        return ProfileTabViewModel()
    }

    override val vmTypeName: String
        get() = ProfileTabViewModel::class.java.typeName

}

class ProfileViewModelFactory(val profileRepository: ProfileRepository) :
    ViewModelFactory<ProfileViewModel> {
    override fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = ProfileViewModel::class.java.typeName

}

class AuthViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<AuthViewModel> {
    override fun createViewModel(): AuthViewModel {
        return AuthViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = AuthViewModel::class.java.typeName

}

class RegViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<RegViewModel> {
    override fun createViewModel(): RegViewModel {
        return RegViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = RegViewModel::class.java.typeName

}

class NotificationsViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<NotificationsViewModel> {
    override fun createViewModel(): NotificationsViewModel {
        return NotificationsViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = NotificationsViewModel::class.java.typeName

}


fun viewModelFactories(
    repositoryFactory: RepositoryFactory
): List<ViewModelFactory<*>> {
    val profileRepository = repositoryFactory.createProfileRepository()
    val favoriteRepository = repositoryFactory.createFavoriteRepository()
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
        ProfileTabViewModelFactory(),
        BookmarkTabViewModelFactory(),
        ProfileViewModelFactory(profileRepository),
        AuthViewModelFactory(profileRepository),
        RegViewModelFactory(profileRepository),
        NotificationsViewModelFactory(profileRepository),
        PostListViewModelFactory(
            feedRepository = repositoryFactory.createFeedRepository(favoriteRepository),
            favoriteRepository = favoriteRepository,
        ),
        BookmarkListViewModelFactory(
            favoriteRepository = favoriteRepository,
        ),
        PostDetailsViewModelFactory(),
    )
}