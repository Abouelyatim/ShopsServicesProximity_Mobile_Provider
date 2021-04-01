package com.smartcity.provider.ui.main.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.util.Constants
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.layout_order_item_header.view.*

class OrderAdapter (
    private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private  var  orderProductRecyclerAdapter: OrderProductAdapter?=null

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            OrderRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class OrderRecyclerChangeCallback(
        private val adapter: OrderAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHolder {
        return OrderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_item_header, parent, false),
            orderProductRecyclerAdapter = orderProductRecyclerAdapter,
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: List<Order>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderHolder(
        itemView: View,
        private var orderProductRecyclerAdapter: OrderProductAdapter?,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView){

        fun initProductsRecyclerView(recyclerview:RecyclerView){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                val topSpacingDecorator = TopSpacingItemDecoration(0)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)

                orderProductRecyclerAdapter =
                    OrderProductAdapter(
                        requestManager
                    )
                addOnScrollListener(object: RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    }
                })
                adapter = orderProductRecyclerAdapter
            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Order) = with(itemView) {

            itemView.order_id.text=item.id.toString()
            itemView.order_time.text=item.createAt

            var total=0.0
            item.orderProductVariants.map {
                total=it.productVariant.price*it.quantity+total
            }
            itemView.order_product_total_price.text=total.toString()+ Constants.DINAR_ALGERIAN


            initProductsRecyclerView(itemView.products_order_recycler_view)
            orderProductRecyclerAdapter?.let {
                it.submitList(
                    item.orderProductVariants.sortedBy { it.productVariant.price }
                )
            }

        }
    }
}