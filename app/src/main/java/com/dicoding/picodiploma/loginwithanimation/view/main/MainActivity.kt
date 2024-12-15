package com.dicoding.picodiploma.loginwithanimation.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recyclerView: RecyclerView

    // Initialize the ViewModel with ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val userPreference = UserPreference.getInstance(applicationContext.dataStore)
                val token: String = runBlocking {
                    userPreference.getUserModel().token // Direct access to the token
                }

                val apiService = ApiConfig.getApiService(token)
                val repository = UserRepository.getInstance(userPreference, apiService)
                val factory = ViewModelFactory(repository, apiService)
                return factory.create(modelClass)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter and recycler view
        storyAdapter = StoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter
        binding.recyclerView.setHasFixedSize(true)

        // Observe story data
        viewModel.getStoryData().observe(this) { storyResponse ->
            if (!storyResponse.error!!) {
                Log.d("MainActivity", "Story Data: ${storyResponse.listStory}")
                storyAdapter.submitList(storyResponse.listStory)
            } else {
                Log.e("MainActivity", "Error fetching story data")
            }
        }

        storyAdapter.submitList(
            listOf(
                ListStoryItem("1", "Title 1", "Description 1", "https://via.placeholder.com/150"),
                ListStoryItem("2", "Title 2", "Description 2", "https://via.placeholder.com/150")
            )
        )



        // Observe user session
        viewModel.getSession().observe(this) { session ->
            val token = session?.token ?: ""
            if (token.isNotEmpty()) {
                // Token is valid, now fetch the story data
                viewModel.getStoryData().observe(this) { storyResponse ->
                    if (!storyResponse.error!!) {
                        Log.d("MainActivity", "Story Data: ${storyResponse.listStory}")
                        storyAdapter.submitList(storyResponse.listStory)
                    } else {
                        Log.e("MainActivity", "Error fetching story data")
                    }
                }
            } else {
                Log.e("MainActivity", "Token is empty")
            }
        }







        setupView()
        setupAction()
        playAnimation()
    }




    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        // Add OnClickListener for addImageButton
        binding.addImageButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val logout = ObjectAnimator.ofFloat(binding.logoutButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(name, message, logout)
            startDelay = 100
        }.start()
    }
}
