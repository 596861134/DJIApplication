package com.czf.dji.pages


import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.czf.dji.keyvalue.CapabilityKeyChecker
import com.czf.dji.R
import com.czf.dji.databinding.FragmentKeyListBinding
import com.czf.dji.keyvalue.KeyItemHelper.processSubListLogic
import com.czf.dji.util.ToastUtils.showToast
import com.czf.dji.util.Util
import dji.sdk.keyvalue.converter.EmptyValueConverter
import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.key.ComponentType
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.common.BoolMsg
import dji.sdk.keyvalue.value.common.CameraLensType
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.sdk.keyvalue.value.product.ProductType
import dji.v5.manager.KeyManager
import dji.v5.manager.capability.CapabilityManager
import dji.v5.utils.common.DjiSharedPreferencesManager
import dji.v5.utils.common.LogUtils
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Collections
import java.util.Date


/**
 * Class Description
 *
 * @author Hoker
 * @date 2021/5/11
 *
 * Copyright (c) 2021, DJI All Rights Reserved.
 */
class KeyValueFragment : DJIFragment(), View.OnClickListener {
    private val TAG = LogUtils.getTag("KeyValueFragment")

    val CAPABILITY_ENABLE = "capabilityenable"
    var currentChannelType: com.czf.dji.keyvalue.ChannelType? = com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_CAMERA
    val LISTEN_RECORD_MAX_LENGTH = 6000
    val HIGH_FREQUENCY_KEY_SP_NAME = "highfrequencykey"
    val LENS_TAG = "CAMERA_LENS_"

    var contentView: View? = null
    var recyclerView: RecyclerView? = null
    var btAction: Button? = null
    val logMessage = StringBuilder()


    var currentKeyItem: com.czf.dji.keyvalue.KeyItem<*, *>? = null
    val currentKeyTypeList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val currentKeyItemList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val currentChannelList = Arrays.asList(*com.czf.dji.keyvalue.ChannelType.values())
    val data: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    var cameraParamsAdapter: com.czf.dji.keyvalue.KeyItemAdapter? = null
    val batteryKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val wifiKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val bleList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val gimbalKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val cameraKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val flightAssistantKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val flightControlKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val airlinkKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val remoteControllerKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val productKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val rtkBaseKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val rtkMobileKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val ocuSyncKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val radarKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val appKeyList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val mobileNetworkKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val mobileNetworkLinkRCKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val batteryBoxKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val onBoardKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val payloadKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    val lidarKeyList: List<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList()
    var keyValuesharedPreferences: SharedPreferences? = null
    val selectMode = false
    var totalKeyCount: Int? = null
    var capabilityKeyCount: Int? = null

    private lateinit var binding: FragmentKeyListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (contentView == null) {
            binding = FragmentKeyListBinding.inflate(inflater, container, false)
            contentView = binding.root
        }
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLocalData()
        contentView?.let { initView(it) }
        initRemoteData()
        val parent = contentView!!.parent as ViewGroup
        parent.removeView(contentView)
    }

    private fun initLocalData() {
        data.clear()
        cameraParamsAdapter = com.czf.dji.keyvalue.KeyItemAdapter(activity, data, itemClickCallback)
        keyValuesharedPreferences =
            activity?.getSharedPreferences(HIGH_FREQUENCY_KEY_SP_NAME, Context.MODE_PRIVATE)
    }


    private fun initView(view: View) {
        initViewAndListener(view)
        binding.includeOperate.tvResult.setOnLongClickListener {
            val cmb = activity
                ?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = binding.includeOperate.tvResult.text.toString()
            true
        }
        binding.includeOperate.btGet.setOnClickListener(this)
        binding.includeOperate.btSet.setOnClickListener(this)
        binding.includeOperate.btListen.setOnClickListener(this)
        binding.includeOperate.btAction.setOnClickListener(this)
        binding.includeOperate.btnClearlog.setOnClickListener(this)
        binding.includeOperate.btUnlistenall.setOnClickListener(this)
        binding.ivQuestionMark.setOnClickListener(this)

        binding.ivCapability.isChecked = isCapabilitySwitchOn()
        msdkInfoVm.msdkInfo.observe(viewLifecycleOwner) {
            binding.ivCapability.isEnabled = it.productType != ProductType.UNRECOGNIZED
            setDataWithCapability(binding.ivCapability.isChecked)
            Schedulers.single().scheduleDirect {
                if (totalKeyCount == null || capabilityKeyCount == null) {
                    totalKeyCount = com.czf.dji.keyvalue.KeyItemDataUtil.getAllKeyListCount();
                    capabilityKeyCount = CapabilityManager.getInstance().getCapabilityKeyCount(it.productType.name)
                }
            }
        }
        binding.includeOperate.spIndex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setKeyInfo()
                currentKeyItem?.let { updateComponentSpinner(it) }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //do nothing
            }
        }
        binding.ivCapability.setOnCheckedChangeListener { _, enable ->
            if (enable) {
                capabilityKeyCount?.let { showToast(binding.tvCapablity.text.toString() + " count:$it") }
            } else {
                totalKeyCount?.let { showToast(binding.tvCapablity.text.toString() + " count:$it") }
            }
            setDataWithCapability(enable)
        }
    }

    private fun initViewAndListener(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        binding.tvOperateTitleLyt.setOnClickListener {
            channelTypeFilterOperate()
        }
        binding.llFilterContainer.setOnClickListener {
            keyFilterOperate()
        }

        btAction = view.findViewById(R.id.bt_action)
        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Do Something
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Do Something
            }

            override fun afterTextChanged(s: Editable) {
                cameraParamsAdapter?.filter?.filter(s.toString())
            }
        })
    }

    private fun initRemoteData() {
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = cameraParamsAdapter
        binding.includeOperate.tvTip.movementMethod = ScrollingMovementMethod.getInstance()
        binding.includeOperate.tvResult.movementMethod = ScrollingMovementMethod.getInstance()
    }

    override fun onResume() {
        processChannelInfo()
        super.onResume()
    }

    /**
     * key列表点击回调
     */
    val itemClickCallback: com.czf.dji.keyvalue.KeyItemActionListener<com.czf.dji.keyvalue.KeyItem<*, *>?> = object :
        com.czf.dji.keyvalue.KeyItemActionListener<com.czf.dji.keyvalue.KeyItem<*, *>?> {

        override fun actionChange(keyItem: com.czf.dji.keyvalue.KeyItem<*, *>?) {
            if (keyItem == null) {
                return
            }
            initKeyInfo(keyItem)
            cameraParamsAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * key操作结果回调
     */
    private val keyItemOperateCallBack: com.czf.dji.keyvalue.KeyItemActionListener<Any> =
        com.czf.dji.keyvalue.KeyItemActionListener<Any> { t -> //  processListenLogic();
            t?.let {
                binding.includeOperate.tvResult.text = appendLogMessageRecord(t.toString())
                scrollToBottom()
            }

        }

    private fun scrollToBottom() {
        val scrollOffset = (binding.includeOperate.tvResult.layout.getLineTop(binding.includeOperate.tvResult.lineCount)
                - binding.includeOperate.tvResult.height)
        if (scrollOffset > 0) {
            binding.includeOperate.tvResult.scrollTo(0, scrollOffset)
        } else {
            binding.includeOperate.tvResult.scrollTo(0, 0)
        }
    }

    private fun appendLogMessageRecord(appendStr: String?): String {
        val curTime = SimpleDateFormat("HH:mm:ss").format(Date())
        logMessage.append(curTime)
            .append(":")
            .append(appendStr)
            .append("\n")

        //长度限制
        var result = logMessage.toString()
        if (result.length > LISTEN_RECORD_MAX_LENGTH) {
            result = result.substring(result.length - LISTEN_RECORD_MAX_LENGTH)
        }
        return result
    }

    /**
     * 推送结果回调
     */
    val pushCallback: com.czf.dji.keyvalue.KeyItemActionListener<String> =
        com.czf.dji.keyvalue.KeyItemActionListener<String> { t -> //  processListenLogic();
            binding.includeOperate.tvResult.text = appendLogMessageRecord(t)
            scrollToBottom()

        }

    /**
     * 初始化Key的信息
     *
     * @param keyItem
     */
    private fun initKeyInfo(keyItem: com.czf.dji.keyvalue.KeyItem<*, *>) {
        currentKeyItem = keyItem
        currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)
        binding.includeOperate.tvName.text = keyItem.name
        binding.includeOperate.btAddCommand.visibility = if (selectMode) View.VISIBLE else View.GONE
        processListenLogic()
        binding.includeOperate.btGpscoord.visibility = View.GONE
        binding.includeOperate.tvTip.visibility = View.GONE
        keyItem.count = System.currentTimeMillis()
        resetSelected()
        binding.includeOperate.btSet.isEnabled = currentKeyItem!!.canSet()
        binding.includeOperate.btGet.isEnabled = currentKeyItem!!.canGet()
        binding.includeOperate.btListen.isEnabled = currentKeyItem!!.canListen()
        binding.includeOperate.btAction.isEnabled = currentKeyItem!!.canAction()
        keyValuesharedPreferences?.edit()?.putLong(keyItem.toString(), keyItem.count)?.apply()
        keyItem.isItemSelected = true

        updateComponentSpinner(keyItem)

    }

    private fun updateComponentSpinner(keyItem: com.czf.dji.keyvalue.KeyItem<*, *>) {
        val componentType = ComponentType.find(keyItem.keyInfo.componentType)
        if (componentType == ComponentType.CAMERA && isCapabilitySwitchOn()) {
            val list = CapabilityManager.getInstance().getSupportLens("Key" + keyItem.name)
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                list
            )
            binding.includeOperate.spSubtype.adapter = adapter
            binding.includeOperate.tvSubtype.text = "lenstype"
            val defalutIndex = list.indexOf("DEFAULT")
            if (defalutIndex != -1) {
                binding.includeOperate.spSubtype.setSelection(defalutIndex)
            }
        } else {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                requireContext().resources.getStringArray(R.array.sub_type_arrays)
            )
            binding.includeOperate.spSubtype.adapter = adapter
            binding.includeOperate.tvSubtype.text = "subtype"
        }
    }

    private fun resetSelected() {
        for (item in data) {
            if (item.isItemSelected) {
                item.isItemSelected = false
            }
        }
    }

    /**
     * 处理Listen显示控件
     */
    private fun processListenLogic() {
        if (currentKeyItem == null) {

            binding.includeOperate.btListen.text = "Listen"
            binding.includeOperate.tvName.text = ""
            return
        }
        val needShowListenView =
            currentKeyItem!!.canListen() && currentKeyItem!!.getListenHolder() is KeyValueFragment && Util.isNotBlank(
                currentKeyItem!!.getListenRecord()
            )
        if (needShowListenView) {
            binding.includeOperate.tvTip.visibility = View.VISIBLE
            binding.includeOperate.tvTip.text = currentKeyItem!!.getListenRecord()
        } else {
            binding.includeOperate.tvTip.visibility = View.GONE
            binding.includeOperate.tvTip.setText(R.string.operate_listen_record_tips)
        }
        if (currentKeyItem!!.getListenHolder() == null) {

            binding.includeOperate.btListen.text = "Listen"
        } else {

            binding.includeOperate.btListen.text = "UNListen"
        }
    }

    /**
     * 根据不同类型入口，初始化不同数据
     */
    private fun processChannelInfo() {
        currentKeyTypeList.clear()
        currentKeyItemList.clear()
        var tips: String? = ""
        when (currentChannelType) {
            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_BATTERY -> {
                com.czf.dji.keyvalue.KeyItemDataUtil.initBatteryKeyList(batteryKeyList)
                tips = Util.getString(R.string.battery)
                currentKeyItemList.addAll(batteryKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_GIMBAL -> {
                com.czf.dji.keyvalue.KeyItemDataUtil.initGimbalKeyList(gimbalKeyList)
                tips = Util.getString(R.string.gimbal)
                currentKeyItemList.addAll(gimbalKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_CAMERA -> {
                com.czf.dji.keyvalue.KeyItemDataUtil.initCameraKeyList(cameraKeyList)
                tips = Util.getString(R.string.camera)
                currentKeyItemList.addAll(cameraKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_FLIGHT_ASSISTANT -> {
                tips = Util.getString(R.string.flight_assistant)
                com.czf.dji.keyvalue.KeyItemDataUtil.initFlightAssistantKeyList(flightAssistantKeyList)
                currentKeyItemList.addAll(flightAssistantKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_FLIGHT_CONTROL -> {
                tips = Util.getString(R.string.flight_control)
                com.czf.dji.keyvalue.KeyItemDataUtil.initFlightControllerKeyList(flightControlKeyList)
                currentKeyItemList.addAll(flightControlKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_AIRLINK -> {
                tips = Util.getString(R.string.airlink)
                com.czf.dji.keyvalue.KeyItemDataUtil.initAirlinkKeyList(airlinkKeyList)
                currentKeyItemList.addAll(airlinkKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_REMOTE_CONTROLLER -> {
                tips = Util.getString(R.string.remote_controller)
                com.czf.dji.keyvalue.KeyItemDataUtil.initRemoteControllerKeyList(remoteControllerKeyList)
                currentKeyItemList.addAll(remoteControllerKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_BLE -> {
                tips = Util.getString(R.string.ble)
                com.czf.dji.keyvalue.KeyItemDataUtil.initBleKeyList(bleList)
                currentKeyItemList.addAll(bleList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_PRODUCT -> {
                tips = Util.getString(R.string.product)
                com.czf.dji.keyvalue.KeyItemDataUtil.initProductKeyList(productKeyList)
                currentKeyItemList.addAll(productKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_RTK_BASE_STATION -> {
                tips = Util.getString(R.string.rtkbase)
                com.czf.dji.keyvalue.KeyItemDataUtil.initRtkBaseStationKeyList(rtkBaseKeyList)
                currentKeyItemList.addAll(rtkBaseKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_RTK_MOBILE_STATION -> {
                tips = Util.getString(R.string.rtkmobile)
                com.czf.dji.keyvalue.KeyItemDataUtil.initRtkMobileStationKeyList(rtkMobileKeyList)
                currentKeyItemList.addAll(rtkMobileKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_OCU_SYNC -> {
                tips = Util.getString(R.string.ocusync)
                com.czf.dji.keyvalue.KeyItemDataUtil.initOcuSyncKeyList(ocuSyncKeyList)
                currentKeyItemList.addAll(ocuSyncKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_RADAR -> {
                tips = Util.getString(R.string.radar)
                com.czf.dji.keyvalue.KeyItemDataUtil.initRadarKeyList(radarKeyList)
                currentKeyItemList.addAll(radarKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_MOBILE_NETWORK -> {
                tips = Util.getString(R.string.mobile_network)
                com.czf.dji.keyvalue.KeyItemDataUtil.initMobileNetworkKeyList(mobileNetworkKeyList)
                com.czf.dji.keyvalue.KeyItemDataUtil.initMobileNetworkLinkRCKeyList(mobileNetworkLinkRCKeyList)
                currentKeyItemList.addAll(mobileNetworkKeyList)
                currentKeyItemList.addAll(mobileNetworkLinkRCKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_ON_BOARD -> {
                tips = Util.getString(R.string.on_board)
                com.czf.dji.keyvalue.KeyItemDataUtil.initOnboardKeyList(onBoardKeyList)
                currentKeyItemList.addAll(onBoardKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_ON_PAYLOAD -> {
                tips = Util.getString(R.string.payload)
                com.czf.dji.keyvalue.KeyItemDataUtil.initPayloadKeyList(payloadKeyList)
                currentKeyItemList.addAll(payloadKeyList)
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_LIDAR -> {
                tips = Util.getString(R.string.lidar)
                com.czf.dji.keyvalue.KeyItemDataUtil.initLidarKeyList(lidarKeyList)
                currentKeyItemList.addAll(lidarKeyList)
            }

            else -> {
               LogUtils.d(TAG , "nothing to do")
            }
        }
        for (item in currentKeyItemList) {
            item.isItemSelected = false;
            val count = keyValuesharedPreferences?.getLong(item.toString(), 0L)
            if (count != null && count != 0L) {
                item.count = count
            }
        }

        binding.tvOperateTitle.text = tips
        setDataWithCapability(isCapabilitySwitchOn())
    }


    private fun setDataWithCapability(enable: Boolean) {
        val showList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList(currentKeyItemList)
        changeCurrentList(enable, showList)
        data.clear()
        data.addAll(showList)
        data.sortWith { o1, o2 -> o1.name?.compareTo(o2.name) ?: 0 }
        resetSearchFilter()
        setKeyCount(showList.size)
        resetSelected()
        cameraParamsAdapter?.notifyDataSetChanged()
        DjiSharedPreferencesManager.putBoolean(context, CAPABILITY_ENABLE, enable)
        if (enable) {
            binding.tvCapablity.text = "Officially released key"
        } else {
            binding.tvCapablity.text = "All key"
        }
    }


    /**
     *  能力集开关打开，并且获取的产品名称在能力集列表中则更新列表
     */
    private fun changeCurrentList(enable: Boolean, showList: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>>) {
        val type = msdkInfoVm.msdkInfo.value?.productType?.name
        if (enable && CapabilityManager.getInstance().isProductSupported(type)) {
            val iterator = showList.iterator();
            while (iterator.hasNext()) {
                if (isNeedRemove("Key" + iterator.next().name)) {
                    iterator.remove()
                }
            }
        }
    }


    private fun isNeedRemove(keyName: String): Boolean {
        var isNeedRemove = false;
        val type = msdkInfoVm.msdkInfo.value?.productType?.name

        val cameraType = KeyManager.getInstance().getValue(
            KeyTools.createKey(
                CameraKey.KeyCameraType,
                CapabilityManager.getInstance().componentIndex
            )
        )

        when (currentChannelType) {
            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_CAMERA -> {
                if (!CapabilityManager.getInstance()
                        .isCameraKeySupported(type, cameraType?.name, keyName)
                ) {
                    isNeedRemove = true
                }
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_AIRLINK -> {
                if (!CapabilityManager.getInstance()
                        .isKeySupported(type, "", ComponentType.AIRLINK, keyName)
                ) {
                    isNeedRemove = true
                }
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_GIMBAL -> {
                if (!CapabilityManager.getInstance()
                        .isKeySupported(type, "", ComponentType.GIMBAL, keyName)
                ) {
                    isNeedRemove = true
                }
            }

            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_REMOTE_CONTROLLER -> {
                if (!CapabilityManager.getInstance()
                        .isKeySupported(type, "", ComponentType.REMOTECONTROLLER, keyName)
                ) {
                    isNeedRemove = true
                }
            }

            else -> {
                if (!CapabilityManager.getInstance()
                        .isKeySupported(type, keyName)
                ) {
                    isNeedRemove = true
                }
            }
        }

        return isNeedRemove
    }

    /**
     * 清空search框
     */
    private fun resetSearchFilter() {
        binding.etFilter.setText("")
        cameraParamsAdapter?.getFilter()?.filter("")
    }

    private fun isCapabilitySwitchOn(): Boolean {
        return DjiSharedPreferencesManager.getBoolean(context, CAPABILITY_ENABLE, false)
    }

    private fun setKeyCount(count: Int) {
        binding.tvCount.text = "(${count})";
    }

    override fun onClick(view: View) {

        if (Util.isBlank(binding.includeOperate.tvName.text?.toString()) || currentKeyItem == null) {
            showToast("please select key first")
            return
        }
        setKeyInfo()

        when (view?.id) {
            R.id.bt_get -> {
                get()
            }

            R.id.bt_unlistenall -> {
                unListenAll()
            }

            R.id.bt_set -> {
                set()
            }

            R.id.bt_listen -> {
                listen()
            }

            R.id.bt_action -> {
                action()
            }

            R.id.btn_clearlog -> {
                binding.includeOperate.tvResult.text = ""
                logMessage.delete(0, logMessage.length)
            }

            R.id.iv_question_mark -> {

                val cameraType = KeyManager.getInstance().getValue(
                    KeyTools.createKey(
                        CameraKey.KeyCameraType,
                        CapabilityManager.getInstance().componentIndex
                    )
                )
                cameraType?.name?.let {
                    CapabilityKeyChecker.check(
                        msdkInfoVm.msdkInfo.value?.productType?.name!!,
                        it
                    )
                }
                // KeyValueDialogUtil.showNormalDialog(getActivity(), "提示")
                //CapabilityKeyChecker.generateAllEnumList(msdkInfoVm.msdkInfo.value?.productType?.name!! , cameraType!!.name )

            }
        }
    }


    /**
     * key列表条件过滤
     */
    private fun keyFilterOperate() {
        val sortlist: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>> = ArrayList(currentKeyItemList)
        changeCurrentList(isCapabilitySwitchOn(), sortlist)
        Collections.sort(sortlist)
        com.czf.dji.keyvalue.KeyValueDialogUtil.showFilterListWindow(
            binding.llChannelFilterContainer,
            sortlist,
            object :
                com.czf.dji.keyvalue.KeyItemActionListener<com.czf.dji.keyvalue.KeyItem<*, *>?> {
                override fun actionChange(item: com.czf.dji.keyvalue.KeyItem<*, *>?) {
                    itemClickCallback.actionChange(item)
                }
            })
    }

    private fun channelTypeFilterOperate() {
        var showChannelList: MutableList<com.czf.dji.keyvalue.ChannelType> = ArrayList()
        val capabilityChannelList = arrayOf(
            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_BATTERY, com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_AIRLINK, com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_CAMERA,
            com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_GIMBAL, com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_REMOTE_CONTROLLER, com.czf.dji.keyvalue.ChannelType.CHANNEL_TYPE_FLIGHT_CONTROL
        )
        if (isCapabilitySwitchOn()) {
            showChannelList = capabilityChannelList.toMutableList()
        } else {
            showChannelList = currentChannelList
        }
        com.czf.dji.keyvalue.KeyValueDialogUtil.showChannelFilterListWindow(
            binding.tvOperateTitle,
            showChannelList
        ) { channelType ->
            currentChannelType = channelType
            currentKeyItem = null
            processChannelInfo()
            processListenLogic()
        }
    }


    private fun getCameraSubIndex(lensName: String): Int {
        CameraLensType.values().forEach {
            if (lensName == it.name) {
                return it.value()
            }
        }
        return CameraLensType.UNKNOWN.value()
    }

    private fun getComponentIndex(compentName: String): Int {
        return when (compentName) {
            ComponentIndexType.LEFT_OR_MAIN.name -> ComponentIndexType.LEFT_OR_MAIN.value()
            ComponentIndexType.RIGHT.name -> ComponentIndexType.RIGHT.value()
            ComponentIndexType.UP.name -> ComponentIndexType.UP.value()
            ComponentIndexType.AGGREGATION.name -> ComponentIndexType.AGGREGATION.value()
            else -> {
                ComponentIndexType.UNKNOWN.value()
            }
        }
    }

    private fun setKeyInfo() {
        if (currentKeyItem == null) {
            return
        }
        try {
            val index = getComponentIndex(binding.includeOperate.spIndex.selectedItem.toString())

            if (index != -1) {
                currentKeyItem!!.componetIndex = index
                CapabilityManager.getInstance().setComponetIndex(index)
            }
            val subtype: Int
            if (ComponentType.find(currentKeyItem!!.keyInfo.componentType) == ComponentType.CAMERA && isCapabilitySwitchOn()) {
                subtype = getCameraSubIndex(LENS_TAG + binding.includeOperate.spSubtype.selectedItem.toString())

            } else {
                subtype = binding.includeOperate.spSubtype.selectedItem.toString().toInt()
            }

            if (subtype != -1) {
                currentKeyItem!!.subComponetType = subtype
            }
            val subIndex = binding.includeOperate.spSubindex.selectedItem.toString().toInt()
            if (subIndex != -1) {
                currentKeyItem!!.subComponetIndex = subIndex
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, e.message)
        }
    }

    /**
     * 获取操作
     */
    private fun get() {
        if (!currentKeyItem?.canGet()!!) {
            showToast("not support get")
            return
        }
        currentKeyItem!!.doGet()
    }

    private fun unListenAll() {
        release()
        processListenLogic()
    }

    /**
     * Listen操作
     */
    private fun listen() {
        if (!currentKeyItem?.canListen()!!) {
            showToast("not support listen")
            return
        }
        currentKeyItem!!.setPushCallBack(pushCallback)
        val listenHolder = currentKeyItem!!.getListenHolder()
        if (listenHolder == null) {
            currentKeyItem!!.listen(this)
            currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)

            binding.includeOperate.btListen.text = "Un-Listen"
        } else if (listenHolder is KeyValueFragment) {
            currentKeyItem!!.cancelListen(this)
            binding.includeOperate.btListen.text = "Listen"
        }
        processListenLogic()
    }

    /**
     * 设置操作
     */
    private fun set() {
        if (!currentKeyItem?.canSet()!!) {
            showToast("not support set")
            return
        }
        if (currentKeyItem!!.param is BoolMsg) {
            processBoolMsgDlg(currentKeyItem!!)
            return
        }
        if (currentKeyItem!!.subItemMap.isNotEmpty()) {
            processSubListLogic(
                binding.includeOperate.btSet,
                currentKeyItem!!.param,
                currentKeyItem!!.subItemMap as Map<String?, List<com.czf.dji.keyvalue.EnumItem>>,
                object :
                    com.czf.dji.keyvalue.KeyItemActionListener<String?> {


                    override fun actionChange(paramJsonStr: String?) {
                        if (Util.isBlank(paramJsonStr)) {
                            return
                        }
                        currentKeyItem!!.doSet(paramJsonStr)
                    }
                })
        } else {
            com.czf.dji.keyvalue.KeyValueDialogUtil.showInputDialog(
                activity,
                currentKeyItem,
                object :
                    com.czf.dji.keyvalue.KeyItemActionListener<String?> {
                    override fun actionChange(s: String?) {
                        if (Util.isBlank(s)) {
                            return
                        }
                        currentKeyItem!!.doSet(s)
                    }
                })
        }
    }

    private fun processBoolMsgDlg(keyitem: com.czf.dji.keyvalue.KeyItem<*, *>) {
        val boolValueList: MutableList<String> = java.util.ArrayList()
        boolValueList.add("false")
        boolValueList.add("true")

        com.czf.dji.keyvalue.KeyValueDialogUtil.showSingleChoiceDialog(
            context,
            boolValueList,
            -1,
            object : com.czf.dji.keyvalue.KeyItemActionListener<List<String>?> {
                override fun actionChange(values: List<String>?) {
                    val param = "{\"value\":${values?.get(0)}}"
                    keyitem.doSet(param)
                }
            })
    }

    /**
     * 动作操作
     */
    private fun action() {
        if (!currentKeyItem?.canAction()!!) {
            showToast("not support action")
            return
        }

        if (currentKeyItem!!.keyInfo.typeConverter === EmptyValueConverter.converter) {
            currentKeyItem?.doAction("")
        } else if (currentKeyItem?.subItemMap!!.isNotEmpty()) {
            processSubListLogic(
                binding.includeOperate.btSet,
                currentKeyItem?.param,
                currentKeyItem?.subItemMap as Map<String?, List<com.czf.dji.keyvalue.EnumItem>>,
                object :
                    com.czf.dji.keyvalue.KeyItemActionListener<String?> {
                    override fun actionChange(paramJsonStr: String?) {
                        if (Util.isBlank(paramJsonStr)) {
                            return
                        }
                        currentKeyItem!!.doAction(paramJsonStr)
                    }
                })
        } else if (currentKeyItem!!.paramJsonStr != null && currentKeyItem!!.paramJsonStr == "{}") {
            currentKeyItem!!.doAction(currentKeyItem!!.paramJsonStr)
        } else {
            com.czf.dji.keyvalue.KeyValueDialogUtil.showInputDialog(
                activity,
                currentKeyItem
            ) { s -> currentKeyItem!!.doAction(s) }
        }
    }

    /**
     * 注销Listen，移除业务回调
     *
     * @param list
     */
    private fun releaseKeyInfo(list: MutableList<com.czf.dji.keyvalue.KeyItem<*, *>>?) {
        if (list == null) {
            return
        }
        for (item in list) {
            item.removeCallBack()
            item.cancelListen(this)
        }

    }

    open fun release() {
        if (currentKeyItem != null) {
            currentKeyItem!!.cancelListen(this)
        }
        releaseKeyInfo(batteryKeyList)
        releaseKeyInfo(gimbalKeyList)
        releaseKeyInfo(cameraKeyList)
        releaseKeyInfo(wifiKeyList)
        releaseKeyInfo(flightAssistantKeyList)
        releaseKeyInfo(flightControlKeyList)
        releaseKeyInfo(airlinkKeyList)
        releaseKeyInfo(productKeyList)
        releaseKeyInfo(rtkBaseKeyList)
        releaseKeyInfo(rtkMobileKeyList)
        releaseKeyInfo(remoteControllerKeyList)
        releaseKeyInfo(radarKeyList)
        releaseKeyInfo(appKeyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }
}