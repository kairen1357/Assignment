import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment_test.KcalViewPagerAdapter
import com.example.assignment_test.ViewPagerAdapter
import com.example.assignment_test.databinding.FragmentReportBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class Fragment_report : Fragment() {
    private lateinit var binding: FragmentReportBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)










        val tabLayout1=binding.tabLayout1
        val viewPager1=binding.viewPager1
        val adapter=ViewPagerAdapter(childFragmentManager,lifecycle)
        viewPager1.adapter=adapter
        TabLayoutMediator(tabLayout1,viewPager1){tab,position->
            when(position){
                0->{
                    tab.text="Day"
                }
                1->{
                    tab.text="Week"
                }
                2->{
                    tab.text="Month"
                }
                3->{
                    tab.text="Year"
                }
            }
        }.attach()

        val tabLayout2=binding.tabLayout2
        val viewPager2=binding.viewPager2
        val adapter2= KcalViewPagerAdapter(childFragmentManager,lifecycle)
        viewPager2.adapter=adapter2
        TabLayoutMediator(tabLayout2,viewPager2){tab,position->
            when(position){
                0->{
                    tab.text="Day"
                }
                1->{
                    tab.text="Week"
                }
                2->{
                    tab.text="Month"
                }
                3->{
                    tab.text="Year"
                }
            }
        }.attach()

    }

}
