// Copyright 2020 Anish Singhani
//
// SPDX-License-Identifier: Apache-2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package tests.aes

import aes128.{Aes128Combined, Aes128Decrypt, Aes128Encrypt}
import chisel3.iotesters.PeekPokeTester

class Aes128DecryptTest(dut: Aes128Combined) extends PeekPokeTester(dut) {

    def setKey(key: BigInt): Unit = {
        while(peek(dut.io.decReady) == 0) step(1)

        poke(dut.io.decDataValid, false)
        poke(dut.io.keyValid, false)
        expect(dut.io.decReady, true)

        poke(dut.io.keyIn, key)
        poke(dut.io.keyValid, true)
        step(1)
        poke(dut.io.keyIn, 0)
        poke(dut.io.keyValid, false)

        while(peek(dut.io.decReady) == 0) step(1)
    }

    def runTest(ciphertext: BigInt, plaintext: BigInt, iv: BigInt = BigInt(0)): Unit = {
        poke(dut.io.decIvIn, iv)
        poke(dut.io.decDataIn, ciphertext)
        poke(dut.io.decDataValid, true)
        step(1)
        poke(dut.io.decDataIn, 0)
        poke(dut.io.decDataValid, false)

        expect(dut.io.decReady, false)

        while(peek(dut.io.decOutputValid) == 0) step(1)

        expect(dut.io.decDataOut, plaintext)
        expect(dut.io.decIvOut, ciphertext)
    }

    poke(dut.io.keyValid, false)
    poke(dut.io.encDataValid, false)
    poke(dut.io.decDataValid, false)

    setKey(BigInt("129445976579865719297921356551604413220"))
    runTest(BigInt("30614575354952859734368363414031006605"), BigInt("138766332635719238849554048983485396278"))

    setKey(BigInt("77480659682196824781536209242280568617"))
    runTest(BigInt("108168270634000394736752434854797242083"), BigInt("141123105032041655060716409566795293791"))
    runTest(BigInt("183741596611713424932424710302674779678"), BigInt("141123105032041655060716409566795293791"), iv=BigInt("82999517049282736278697283449288677672"))
    runTest(BigInt("300612637423229317006463393945750264978"), BigInt("53533723263096188494879905935507858290"))
    runTest(BigInt("191269884928480092850775886794089116221"), BigInt("53533723263096188494879905935507858290"), iv=BigInt("114569600669659166774737151663344146729"))
    runTest(BigInt("103522583274007989084648569150893738254"), BigInt("78732626654880918153396673645234369074"))
    runTest(BigInt("285620521974381805374183171356809032743"), BigInt("78732626654880918153396673645234369074"), iv=BigInt("147981112317427754652321488608020016728"))
    runTest(BigInt("193102151738919742469920448191206198811"), BigInt("138531862892119456316820873688573960762"))
    runTest(BigInt("173577793028898773428655398731547059119"), BigInt("138531862892119456316820873688573960762"), iv=BigInt("68078054275351338050152113982098793760"))
    runTest(BigInt("49706086182423641475700416514958935121"), BigInt("86919746956773577147522875398327975231"))
    runTest(BigInt("338193151935625951414209024766731325899"), BigInt("86919746956773577147522875398327975231"), iv=BigInt("154741751010537385352629634512842542156"))
    runTest(BigInt("273970198091322108497940377693739940962"), BigInt("46702040923631624215828739081448146268"))
    runTest(BigInt("299802157337694593480239856723942797290"), BigInt("46702040923631624215828739081448146268"), iv=BigInt("56436034915973938129463560814026651482"))
    runTest(BigInt("305301111024692185405192604746316717627"), BigInt("138412016849774513588083447828832601662"))
    runTest(BigInt("57413398763412829033508693823403602423"), BigInt("138412016849774513588083447828832601662"), iv=BigInt("81385342179464758397526960020967009100"))
    runTest(BigInt("319918871893428942912044997871517501512"), BigInt("58798465798788617286187471253323784794"))
    runTest(BigInt("338890068702378173717346556928957444045"), BigInt("58798465798788617286187471253323784794"), iv=BigInt("143776593973630143943154948444511161644"))
    runTest(BigInt("44068720614617756832814590939415535974"), BigInt("54966635647002074000128691250454800421"))
    runTest(BigInt("189306379488823371525733209166871985735"), BigInt("54966635647002074000128691250454800421"), iv=BigInt("161009620550989844366415300478312857177"))
    runTest(BigInt("225953404709400016733905290486963264184"), BigInt("102688976646804611198872028837500956983"))
    runTest(BigInt("235450239393449506642395731908503610305"), BigInt("102688976646804611198872028837500956983"), iv=BigInt("129119202813329063617632904645620280672"))
    setKey(BigInt("168133230406563450476682181076666438756"))
    runTest(BigInt("121046064677213715129910243202851277968"), BigInt("93593226920763071136200314498274042728"))
    runTest(BigInt("34875740548325074932055818403492029782"), BigInt("93593226920763071136200314498274042728"), iv=BigInt("155786311412828636019630530021726381664"))
    runTest(BigInt("221439683172403484584374212531183795953"), BigInt("76152237288063830038288918103089040480"))
    runTest(BigInt("192361180064584820718034311861542257461"), BigInt("76152237288063830038288918103089040480"), iv=BigInt("125166182556499340817510211802438925420"))
    runTest(BigInt("74985410665911129249998168335280066645"), BigInt("149129281671248594025131726437087407404"))
    runTest(BigInt("197011348921436014465592423086153160943"), BigInt("149129281671248594025131726437087407404"), iv=BigInt("92138146400676698925533676820066025825"))
    runTest(BigInt("336411266834495581695211615251491155443"), BigInt("89370466470202898584265419397122909237"))
    runTest(BigInt("183771092191819226371557049640011960929"), BigInt("89370466470202898584265419397122909237"), iv=BigInt("90851571057651678177950538685751314534"))
    runTest(BigInt("155127082327735341207477873491389170058"), BigInt("66997751964841502287845889010601511772"))
    runTest(BigInt("60429934501128318513677329122238751357"), BigInt("66997751964841502287845889010601511772"), iv=BigInt("65414000424156128282626910190637031746"))
    runTest(BigInt("268674840304705095827250373774986920998"), BigInt("58783193458783079173709342353718273359"))
    runTest(BigInt("313710977530523120309889064278111997927"), BigInt("58783193458783079173709342353718273359"), iv=BigInt("102570607952530102408400950689181353795"))
    runTest(BigInt("248093015730979054343306269993792335245"), BigInt("97565152593092822743964834447279800693"))
    runTest(BigInt("91602078687523601706676496344237769964"), BigInt("97565152593092822743964834447279800693"), iv=BigInt("117212818931004469074149527587674335309"))
    runTest(BigInt("72297915303861513645483793112259038184"), BigInt("155780739454658642545025277187422105953"))
    runTest(BigInt("244258284097259529814702152778571909950"), BigInt("155780739454658642545025277187422105953"), iv=BigInt("88057408475640289260295645753053619494"))
    runTest(BigInt("272507431816944435304418032931387588226"), BigInt("60321314906378619420739591942395099737"))
    runTest(BigInt("19423582617831845342411194426754739263"), BigInt("60321314906378619420739591942395099737"), iv=BigInt("97636745044926937982287502739840658474"))
    runTest(BigInt("216963095002947821898717439135212282803"), BigInt("149233616386770394077341237991564465958"))
    runTest(BigInt("252439460579078679035200523454012558168"), BigInt("149233616386770394077341237991564465958"), iv=BigInt("57573469417928949309095689639424175160"))
    setKey(BigInt("56160699756946168412993830783233307768"))
    runTest(BigInt("91319044268507202941972982528125592367"), BigInt("157094285742560144584515691291308541754"))
    runTest(BigInt("58272163462201180637127154925730537976"), BigInt("157094285742560144584515691291308541754"), iv=BigInt("128235963857592581060982012888764208212"))
    runTest(BigInt("93057884392798539643087051781756304813"), BigInt("104112005263866868585125124246062529112"))
    runTest(BigInt("311972801436445417688656002192656260735"), BigInt("104112005263866868585125124246062529112"), iv=BigInt("163679255727019089399495422332877816153"))
    runTest(BigInt("258651071398957814557974115102203116336"), BigInt("57444799401210608595779731072236791360"))
    runTest(BigInt("275541323168088084833767381193884547263"), BigInt("57444799401210608595779731072236791360"), iv=BigInt("120202408642463565744350490537146086434"))
    runTest(BigInt("136788408867072678441724129977921913587"), BigInt("68248284768810829628712985356461289022"))
    runTest(BigInt("207878078733511123905568932292321954326"), BigInt("68248284768810829628712985356461289022"), iv=BigInt("52199627951646742468175473887634272882"))
    runTest(BigInt("183792194621724460666356200851159090030"), BigInt("93613122684860310092608537721687865919"))
    runTest(BigInt("115152337288826154479938072803799486340"), BigInt("93613122684860310092608537721687865919"), iv=BigInt("64344915408007433018055461088765622325"))
    runTest(BigInt("88736425531556991548727597981322491153"), BigInt("77517814516065971136599711406871033927"))
    runTest(BigInt("317804638760997730754129928806850804779"), BigInt("77517814516065971136599711406871033927"), iv=BigInt("149269393755405451055286544068123588406"))
    runTest(BigInt("89397704717243510510921253056554740032"), BigInt("157421825030499869138860910285727485790"))
    runTest(BigInt("249133738111446420596201328580097382495"), BigInt("157421825030499869138860910285727485790"), iv=BigInt("153428788155735704736536101081824508016"))
    runTest(BigInt("338986563476707887288117558118205449061"), BigInt("56094297204367439760313495640206164083"))
    runTest(BigInt("295510221268681899058588624049565952104"), BigInt("56094297204367439760313495640206164083"), iv=BigInt("129418860844048651843991228230176043876"))
    runTest(BigInt("31605631744150945753881720822189389965"), BigInt("158750469677049031070085030130279276605"))
    runTest(BigInt("150894039406067137967702080703014866447"), BigInt("158750469677049031070085030130279276605"), iv=BigInt("132234544962886502111496939506174290479"))
    runTest(BigInt("105615805802181541627712285310508083605"), BigInt("79942414420256167296090244935803495759"))
    runTest(BigInt("322611424282754443442980255155913911907"), BigInt("79942414420256167296090244935803495759"), iv=BigInt("90965782266505390799396354484309348442"))
    setKey(BigInt("92185204046469248167003827905140175418"))
    runTest(BigInt("121333428703124735283448471571176000052"), BigInt("89610853974444876801983955311365727278"))
    runTest(BigInt("80677997667537793636786952970495308820"), BigInt("89610853974444876801983955311365727278"), iv=BigInt("145267027048016269350315243415662645071"))
    runTest(BigInt("110300671096566269231507841908942543383"), BigInt("90903595680593655325194095164503500364"))
    runTest(BigInt("85887943042005449039930077680577335597"), BigInt("90903595680593655325194095164503500364"), iv=BigInt("96146904914929916328884187100490589749"))
    runTest(BigInt("17794887987274703494954470280535395636"), BigInt("85294983590046909548240364100500213332"))
    runTest(BigInt("210937110046304912631179985577672115619"), BigInt("85294983590046909548240364100500213332"), iv=BigInt("96214421267512700119798726158332477006"))
    runTest(BigInt("327608243532775836746813337676556230646"), BigInt("99979831846020748490039149995126058292"))
    runTest(BigInt("110424568552530260426749559093847229479"), BigInt("99979831846020748490039149995126058292"), iv=BigInt("129394234188093885347187353772677811038"))
    runTest(BigInt("22795105814766179509981416313128529676"), BigInt("120027332953859210947862750458489298010"))
    runTest(BigInt("206707458266058554223446021465145452853"), BigInt("120027332953859210947862750458489298010"), iv=BigInt("106646680405312600645775565770074104170"))
    runTest(BigInt("48611396679004374987780443224122376209"), BigInt("95882827649749833811538483133734221890"))
    runTest(BigInt("36179591796360429103941314153602938116"), BigInt("95882827649749833811538483133734221890"), iv=BigInt("128178260318694797924493255468611039030"))
    runTest(BigInt("241450782817719980663258134728749764619"), BigInt("152015774314141215589027466845731183472"))
    runTest(BigInt("199505063410155823496416944532215927162"), BigInt("152015774314141215589027466845731183472"), iv=BigInt("85674002162058748982523778889529518689"))
    runTest(BigInt("160241503893090258414264378041155139950"), BigInt("161326681352219763573856799211738905920"))
    runTest(BigInt("36586087455337360815614763808496475851"), BigInt("161326681352219763573856799211738905920"), iv=BigInt("123838620099762686978568658919038477438"))
    runTest(BigInt("171986383207850432375531714415643851411"), BigInt("88347670751910369777042739455739840041"))
    runTest(BigInt("167019280346273728542065769352695656737"), BigInt("88347670751910369777042739455739840041"), iv=BigInt("134902247015043231754705314005773663807"))
    runTest(BigInt("165479082659584696327259040417968417225"), BigInt("68264655281696867382167882676423253825"))
    runTest(BigInt("253431055333524116072307691606499401385"), BigInt("68264655281696867382167882676423253825"), iv=BigInt("66645689756540608723665436680890633331"))
    setKey(BigInt("102626789528879492192772620190984791911"))
    runTest(BigInt("340027509985239814787202406897403428177"), BigInt("116044251804824037003218315543461176126"))
    runTest(BigInt("289809228400017368608673988316175467144"), BigInt("116044251804824037003218315543461176126"), iv=BigInt("52480988245921248213947951517437349754"))
    runTest(BigInt("146941046726767688084672764659223598420"), BigInt("165163279218311259617260194692912529989"))
    runTest(BigInt("152493513888076858240438578666155991296"), BigInt("165163279218311259617260194692912529989"), iv=BigInt("100342238972570657484514481395636989739"))
    runTest(BigInt("260522343698694016771736622328821610073"), BigInt("46861985392670434336865244197061731360"))
    runTest(BigInt("29759189951311839852009936175221063743"), BigInt("46861985392670434336865244197061731360"), iv=BigInt("69671584792788931437195231603032814460"))
    runTest(BigInt("175776363727185919573801589330903663019"), BigInt("112129808557357201833726868714788699249"))
    runTest(BigInt("159203627430800705964549725731511695120"), BigInt("112129808557357201833726868714788699249"), iv=BigInt("158808537245211502170455181388165967414"))
    runTest(BigInt("10706083412761616853096240037900281360"), BigInt("163698520771175324425551156943618849893"))
    runTest(BigInt("322934826597557629129663892003525096970"), BigInt("163698520771175324425551156943618849893"), iv=BigInt("132099419966868799468002745877621864762"))
    runTest(BigInt("7023283209908365614806532471071544453"), BigInt("83940098573327227954855345153330333543"))
    runTest(BigInt("247001799363256573516126099010558376311"), BigInt("83940098573327227954855345153330333543"), iv=BigInt("84147605207506277187239023219696414007"))
    runTest(BigInt("331952614415069656888067185747093346471"), BigInt("154664816955179460876287686648014711117"))
    runTest(BigInt("2378645741288509544359333222401680562"), BigInt("154664816955179460876287686648014711117"), iv=BigInt("45407434765153274576163109756005212494"))
    runTest(BigInt("328430599425910004391420395034604220456"), BigInt("46727798219195563666307148627615837542"))
    runTest(BigInt("49302299509724059971485467714373474338"), BigInt("46727798219195563666307148627615837542"), iv=BigInt("49420976039755159946977451604464386665"))
    runTest(BigInt("137973728322967666630215072846187305231"), BigInt("102844501791966117325483077316203128901"))
    runTest(BigInt("119054615975283245246129631046525920509"), BigInt("102844501791966117325483077316203128901"), iv=BigInt("114864929972559519287046553144577653337"))
    runTest(BigInt("69704198091334296993393187962726420747"), BigInt("115913853024372848276413735932130178906"))
    runTest(BigInt("145485800239449590050147346504636053884"), BigInt("115913853024372848276413735932130178906"), iv=BigInt("110691278327582271780158891759776908130"))
    setKey(BigInt("72371946312777826823047635421189984567"))
    runTest(BigInt("47451509507606025795590126179312448566"), BigInt("98552175999019180291032691678900331617"))
    runTest(BigInt("317259421617927864249237040730052016950"), BigInt("98552175999019180291032691678900331617"), iv=BigInt("113459035036743823786403003360551905615"))
    runTest(BigInt("327288159176558439372270600859672150331"), BigInt("102792634975393036091043273828071711290"))
    runTest(BigInt("27999808545786645475803131469809318453"), BigInt("102792634975393036091043273828071711290"), iv=BigInt("162666208457283418848216243288581349744"))
    runTest(BigInt("67938622748223251327654443049609739260"), BigInt("63067894395886938209274346555409594429"))
    runTest(BigInt("322260863064910983941213516932027842464"), BigInt("63067894395886938209274346555409594429"), iv=BigInt("65782838167350059262623551610450161761"))
    runTest(BigInt("106413006702407876128703311662879916496"), BigInt("144035520339580726673100718378411044141"))
    runTest(BigInt("232542914569568904331338194482279568220"), BigInt("144035520339580726673100718378411044141"), iv=BigInt("73737130903744852891797979020949594171"))
    runTest(BigInt("124287245793617218272202872218311811971"), BigInt("142680165180699600875160873160048126003"))
    runTest(BigInt("99082486401212266470980323429444112415"), BigInt("142680165180699600875160873160048126003"), iv=BigInt("93453422256024749548943282178983151394"))
    runTest(BigInt("126532824287672057025056093116721567516"), BigInt("158610479933901662499852034630893320821"))
    runTest(BigInt("303537528550198892453188901615636847745"), BigInt("158610479933901662499852034630893320821"), iv=BigInt("56394639303388908072475290276915914823"))
    runTest(BigInt("311627793362371470943286400577240763714"), BigInt("65460674746939803404477981034465678195"))
    runTest(BigInt("43973358587511645082874833171314577317"), BigInt("65460674746939803404477981034465678195"), iv=BigInt("143890778803731084414899009792022629496"))
    runTest(BigInt("84526661177030380365137084589626508042"), BigInt("92086300143160381412550669101290902372"))
    runTest(BigInt("64496679558146299957998602172244134319"), BigInt("92086300143160381412550669101290902372"), iv=BigInt("166483016464211654765576517618405364062"))
    runTest(BigInt("167827896049480262857480608866464937122"), BigInt("153267219189220364391555576584894424933"))
    runTest(BigInt("196945031861658272688438591641689103951"), BigInt("153267219189220364391555576584894424933"), iv=BigInt("166347793026878177739098938708770970998"))
    runTest(BigInt("32307497040358300714072737686052246715"), BigInt("70855431564589664786409403706491807053"))
    runTest(BigInt("235806789132579456315413839202320055937"), BigInt("70855431564589664786409403706491807053"), iv=BigInt("108052698290941312870903780463894747488"))
    setKey(BigInt("101298229501459450433158723037113507444"))
    runTest(BigInt("256837608255796565526646012362046667241"), BigInt("126600883137701468943312594562149016885"))
    runTest(BigInt("314190579764420591154332669004645914448"), BigInt("126600883137701468943312594562149016885"), iv=BigInt("69324635338755957891425311132660493121"))
    runTest(BigInt("176116554619107839454858422135623184027"), BigInt("84262205512326469101980240326689581090"))
    runTest(BigInt("38852464157961688743941700694315834986"), BigInt("84262205512326469101980240326689581090"), iv=BigInt("49385443879406162496985672220589241149"))
    runTest(BigInt("244716643332153958120163237310711841320"), BigInt("50886949700810371637941141151163311174"))
    runTest(BigInt("207315135854042161424280751809287250167"), BigInt("50886949700810371637941141151163311174"), iv=BigInt("84168924398756875247340801122803798071"))
    runTest(BigInt("131492934948098474060611107051499609542"), BigInt("116194768669506161058449428263923885672"))
    runTest(BigInt("210441415124088915355595341734280442043"), BigInt("116194768669506161058449428263923885672"), iv=BigInt("74905841922983967326855468090393452097"))
    runTest(BigInt("103500455846648436573976737763827782214"), BigInt("165277202109631148755885064390715535927"))
    runTest(BigInt("208052404622306747625968628867481187965"), BigInt("165277202109631148755885064390715535927"), iv=BigInt("117425199871773598792471226937616324166"))
    runTest(BigInt("86056897790213298628391604401489189889"), BigInt("127980512919709846289811648672549662298"))
    runTest(BigInt("180868647837612763955692350954411539692"), BigInt("127980512919709846289811648672549662298"), iv=BigInt("138433170915619705686720461023222647366"))
    runTest(BigInt("303269279032061188322774249989328952947"), BigInt("81649766638020268243865897382754734153"))
    runTest(BigInt("163574714225501438841096257570279775697"), BigInt("81649766638020268243865897382754734153"), iv=BigInt("112030343520359925939901082176394707039"))
    runTest(BigInt("292703679822832360615760052737819590861"), BigInt("127970345573409053591349770153480904818"))
    runTest(BigInt("283205457993435695454841245724228129028"), BigInt("127970345573409053591349770153480904818"), iv=BigInt("122663595306167681641673293065725432371"))
    runTest(BigInt("295529016657626839923715058621428469084"), BigInt("154774122443740348039978585272750207531"))
    runTest(BigInt("244550407000854470087451844617234509241"), BigInt("154774122443740348039978585272750207531"), iv=BigInt("128017873518730288378394163923757379137"))
    runTest(BigInt("324960199637395794709697764942029435760"), BigInt("94963929836409858009552574665745775659"))
    runTest(BigInt("146669575497660633028979986028739956755"), BigInt("94963929836409858009552574665745775659"), iv=BigInt("45558314970768968670011900336261777474"))
    setKey(BigInt("151778268097359500324393864993651958632"))
    runTest(BigInt("328796744800758796582865650384446348643"), BigInt("45423960236857848167707537234490242154"))
    runTest(BigInt("153368785437618761832833525981277989070"), BigInt("45423960236857848167707537234490242154"), iv=BigInt("134803654704993497951112651813754198637"))
    runTest(BigInt("271572883651420180760934586847669852846"), BigInt("134503777526792601139941357986574522483"))
    runTest(BigInt("324127614443984772269455054125368971651"), BigInt("134503777526792601139941357986574522483"), iv=BigInt("76172723053378107227274201275325315875"))
    runTest(BigInt("337875252960369954026359401471074432294"), BigInt("120167135472892874716299241227017602377"))
    runTest(BigInt("141906483173173506494034647488981664629"), BigInt("120167135472892874716299241227017602377"), iv=BigInt("138469683364347159375290058361821079076"))
    runTest(BigInt("24969744755649791476781793233943259297"), BigInt("159913662772991504311349462948438176555"))
    runTest(BigInt("191342019853984141186831496431347503343"), BigInt("159913662772991504311349462948438176555"), iv=BigInt("145152717144438698543392349737959768402"))
    runTest(BigInt("224930683414374644275439462610310772086"), BigInt("130484635533821702032701657867462917462"))
    runTest(BigInt("238429615178094310760580662429937316299"), BigInt("130484635533821702032701657867462917462"), iv=BigInt("62729851278259827936275771562815485276"))
    runTest(BigInt("290935993246917664309957380021722860054"), BigInt("45604861494355918592175353226243171188"))
    runTest(BigInt("310678747872641508969849831267002645966"), BigInt("45604861494355918592175353226243171188"), iv=BigInt("163792714729096551421276492927832507989"))
    runTest(BigInt("54740073074467118009824422870338097327"), BigInt("106578753498258226152585011210371420477"))
    runTest(BigInt("60403869948099898260410962648554515928"), BigInt("106578753498258226152585011210371420477"), iv=BigInt("54998840857301624892293277978869910872"))
    runTest(BigInt("135782446389636432527237547295249495583"), BigInt("89245896336258371150337355278912683560"))
    runTest(BigInt("330736219120584670256656390182234872333"), BigInt("89245896336258371150337355278912683560"), iv=BigInt("146502469354526604196059350010064023912"))
    runTest(BigInt("184300654540174015815626003384858318470"), BigInt("122550545838212052862962181828670399842"))
    runTest(BigInt("314231190461150535304630299605250915496"), BigInt("122550545838212052862962181828670399842"), iv=BigInt("155821767291921994923061601940779990308"))
    runTest(BigInt("2466864203503170557826777364542462656"), BigInt("146559017731316283799586446672888615204"))
    runTest(BigInt("55492619614748930380615305809026792151"), BigInt("146559017731316283799586446672888615204"), iv=BigInt("57568113903315826678869436216028123749"))
    setKey(BigInt("122544642614258461999990035278047240296"))
    runTest(BigInt("265622692371007210627112904032060888790"), BigInt("145085642169790026724237839496092205637"))
    runTest(BigInt("102764897089898617479158702884126448371"), BigInt("145085642169790026724237839496092205637"), iv=BigInt("76276974421161882521466857594460528486"))
    runTest(BigInt("328758449572761206149128509196866545380"), BigInt("56229616936448104810736219418608889401"))
    runTest(BigInt("329386688979954665993179468503850629169"), BigInt("56229616936448104810736219418608889401"), iv=BigInt("69458007314871913753942480703243635274"))
    runTest(BigInt("118875775523949298939014156788778960909"), BigInt("102762761208714701754625762552136292733"))
    runTest(BigInt("111148659917772639362428688996377983519"), BigInt("102762761208714701754625762552136292733"), iv=BigInt("95883255008617416412255539959019675979"))
    runTest(BigInt("335031555026186349324846795105078935307"), BigInt("127773101345088384450597710485347649910"))
    runTest(BigInt("108891005509366776281334765836414600001"), BigInt("127773101345088384450597710485347649910"), iv=BigInt("71976416422625886835176661647731873382"))
    runTest(BigInt("174339460602451737817176534092843846589"), BigInt("97460251077183092241216075192635255163"))
    runTest(BigInt("285512419633606014804101511530227713069"), BigInt("97460251077183092241216075192635255163"), iv=BigInt("78664279634306578873140340329176513614"))
    runTest(BigInt("223204051926619220164087633514176470110"), BigInt("150563289248050676103322475974920321621"))
    runTest(BigInt("329864119268078370883566594191851445733"), BigInt("150563289248050676103322475974920321621"), iv=BigInt("141414703357490261746213758272612947287"))
    runTest(BigInt("114735096945341566339082304267345388801"), BigInt("53503034155721630111415570707627394656"))
    runTest(BigInt("236005268172500719406205787956631702490"), BigInt("53503034155721630111415570707627394656"), iv=BigInt("74626028887466310397176942181640984670"))
    runTest(BigInt("16180103529852854688426954060515704903"), BigInt("134434739312646205586833235363155110493"))
    runTest(BigInt("156302178642738770077824260924142129131"), BigInt("134434739312646205586833235363155110493"), iv=BigInt("93390526735649452880053621959128592234"))
    runTest(BigInt("311170235980418309730563324770381151640"), BigInt("80144085030649257436578456070751616546"))
    runTest(BigInt("58083050751014652342071623299015594037"), BigInt("80144085030649257436578456070751616546"), iv=BigInt("104076352409486066658729080071692104052"))
    runTest(BigInt("59906195094413251872856552144285723846"), BigInt("84054229514383031267729964054687542313"))
    runTest(BigInt("233080732405349040224664440809462324323"), BigInt("84054229514383031267729964054687542313"), iv=BigInt("147893756849228520099643821629814045287"))
    setKey(BigInt("52079816379513698496168418014638996514"))
    runTest(BigInt("213066691207088116118073527151968345637"), BigInt("125317287219454896044025154156890190963"))
    runTest(BigInt("137907716521757683972959115525708562354"), BigInt("125317287219454896044025154156890190963"), iv=BigInt("50845166344109656055292568731053880908"))
    runTest(BigInt("19373440445655058585154005924817389474"), BigInt("113188164337880968408561611100190171950"))
    runTest(BigInt("145667346612196480976257510140047168756"), BigInt("113188164337880968408561611100190171950"), iv=BigInt("159909625550332513665317295682746857000"))
    runTest(BigInt("287440588537841851727745602123595578276"), BigInt("157391059785790237752611573731341444979"))
    runTest(BigInt("126356437332994045571529667056048927264"), BigInt("157391059785790237752611573731341444979"), iv=BigInt("86812023208966408689864336317314381890"))
    runTest(BigInt("213526283591722379830939948295499334496"), BigInt("97389078603945013086722119420873028683"))
    runTest(BigInt("20042552440624916306391919525058805324"), BigInt("97389078603945013086722119420873028683"), iv=BigInt("70716211069288671151242553044721626467"))
    runTest(BigInt("131884007881189291852520540695452019329"), BigInt("80362931030128202144846786694629437289"))
    runTest(BigInt("64983950955322363964431827975734759230"), BigInt("80362931030128202144846786694629437289"), iv=BigInt("70949049408731357541189397085199035984"))
    runTest(BigInt("43365203886527483682457196963314377187"), BigInt("154789232033598153642149786175146765157"))
    runTest(BigInt("74773503306927919193992226555220079110"), BigInt("154789232033598153642149786175146765157"), iv=BigInt("165412024368391335924410944632355119673"))
    runTest(BigInt("338342245269569373307747901278775697916"), BigInt("97294641008858071605151749741242037569"))
    runTest(BigInt("123230385979941268676221393707222860600"), BigInt("97294641008858071605151749741242037569"), iv=BigInt("84235611927222873697147125908010987598"))
    runTest(BigInt("117715882346664645914366167339922367207"), BigInt("60233100838855978268718611088511750724"))
    runTest(BigInt("13025089639313052236411655293914531177"), BigInt("60233100838855978268718611088511750724"), iv=BigInt("44074334470605085630953432628871713630"))
    runTest(BigInt("291044802907760583242047431167292036648"), BigInt("108188489910181862414294570604104082722"))
    runTest(BigInt("111021414059393632641153639994878115443"), BigInt("108188489910181862414294570604104082722"), iv=BigInt("78935104776335234454508588488548691063"))
    runTest(BigInt("123174874366207863936385599532226021310"), BigInt("48088059960293530227446720088654305143"))
    runTest(BigInt("201851808223422530896560535530939776840"), BigInt("48088059960293530227446720088654305143"), iv=BigInt("46824907959208716496977543540148042843"))

}
