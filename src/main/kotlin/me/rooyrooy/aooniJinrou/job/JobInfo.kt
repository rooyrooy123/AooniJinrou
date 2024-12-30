package me.rooyrooy.aooniJinrou.job

import org.bukkit.Material


enum class JobInfo(val displayName: String, val description: String,val item : Material) {
    AOONI(
        displayName = "§1青鬼",
        description = "§b青鬼はチェストから装備を回収し、一式そろうと、§1青鬼の杖§bを獲得。" +
                "杖を右クリックで青鬼に変身し、狩人以外を襲えます。なお、§c§nチェストから装備を取ると、もやもやが発生するため、注意。" +
                "§c§n青鬼陣営・卓郎(第三陣営)§c§l§nのみ走れる。",
        item = Material.BLUE_WOOL
    ),
    HIROSHI(
        displayName = "§dひろし",
        description = "§bひろしは、チェストから鍵の欠片などを回収しまくり、地下室や5階の鍵をつくろう！" +
                "5階で獲得できる§f§n銀の鍵§bと、5階解放時に§b§n1~4階§bのどこかのチェストに生成される§n金の鍵を用意することで、館の鍵を獲得可能。" +
                "脱出を目指そう！",
        item = Material.PINK_WOOL
    ),
    HUNTER(
        displayName = "§c狩人",
        description = "§c狩人は§b§n無敵§bで、§c§n手持ちの弓でプレイヤーを一撃で撃破できます§4(※クールタイムあり)。" +
                "§a§n弓のクールタイム中以外は走ることができます！§bひろしを誤射すると、一定時間、青鬼以外に盲目が付与されます。",
        item = Material.RED_WOOL
    ),
    MIKA(
        displayName = "§5美香青鬼",
        description = "§b美香青鬼は§1青鬼の仲間で、§bダミーアイテムを所持。チェストから防具を獲得できるが、" +
                "§c§n杖にはならないので青鬼に渡しましょう。§4青鬼と美香はお互い正体を知りません。" +
                "§b5階解放まで、チェストの近くだと隠れ玉のタイマーの減りが遅くなります。",
        item = Material.PURPLE_WOOL
    ),
    TAKESHI(
        displayName = "§eたけし",
        description = "§eたけしは§e§l§n第三陣営§bで青鬼、ひろしの勝利時に180秒間、" +
                "§e§l§nタンス(館全体のタンス、クローゼット)の上に滞在していたまま生存していると勝利。",
        item = Material.YELLOW_WOOL

    ),
    TAKUROU(
        displayName = "§a卓郎",
        description = "§a卓郎は§c§l§n第三陣営。§a§l青鬼に1回殴られ、なおかつ狩人に射抜かれると勝利。" +
                "§b2回目以降青鬼に殴られると死亡。一人目の鬼は２回目の攻撃をしても殺せない。",
        item = Material.LIME_WOOL
    );
}

