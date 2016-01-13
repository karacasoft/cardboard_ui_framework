# cardboard_ui_framework
UI framework project for Cardboard. It may have some inconsistent behaviour because it's a little bit old.

#Information
After building my first Cardboard VR project (Video Player for Cardboard), I thought I should simplify the UI creation process in my next applications. So I rewrote the "Video Player for Cardboard" while building this UI framework. I built it to meet my needs in the application. If it doesn't meet your needs, at least send a message to me so that I will work on adding your request if I ever have time to work on this project.<br />
The framework may have inconsistent behaviour because it was written by old me who was a little child that has little to no experience in developing frameworks. Never thought about publishing this framework, too. Though sometimes I still look at my code and say: "Wow, I have done a great job." :V<br />

#Usage
<ol>
<li>Add the *.aar file.(<a href="https://github.com/triforce930/cardboard_ui_framework/raw/master/aar_file_here/cardboardui-release.aar">link</a>)</li>
<li>Extend <code>CardboardUIActivity.</code></li>
<li>Create a <code>ViewContent</code></li>
<li>Create some views.(<code>TextView3D</code>, <code>Button3D</code>, ...)</li>
<li>Use <code>ViewContent.addView(View3D)</code></li>
<li>Use <code>setCurrentContent(ViewContent)</code></li>
</ol>

You should check out the example usages in test_activities package to understand it better. (<a href="https://github.com/triforce930/cardboard_ui_framework/tree/master/com/karacasoft/cardboardui/test_activities">link</a>) AdapterView's may work unexpectedly.<br />
Should you have any questions, please do not hesitate to ask me.
