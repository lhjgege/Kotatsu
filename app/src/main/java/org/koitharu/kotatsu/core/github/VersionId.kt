package org.koitharu.kotatsu.core.github

import java.util.*

data class VersionId(
	val major: Int,
	val minor: Int,
	val build: Int,
	val variantType: String,
	val variantNumber: Int
) : Comparable<VersionId> {

	override fun compareTo(other: VersionId): Int {
		var diff = major.compareTo(other.major)
		if (diff != 0) {
			return diff
		}
		diff = minor.compareTo(other.minor)
		if (diff != 0) {
			return diff
		}
		diff = build.compareTo(other.build)
		if (diff != 0) {
			return diff
		}
		diff = variantWeight(variantType).compareTo(variantWeight(other.variantType))
		if (diff != 0) {
			return diff
		}
		return variantNumber.compareTo(other.variantNumber)
	}

	companion object {

		private fun variantWeight(variantType: String) =
			when (variantType.toLowerCase(Locale.ROOT)) {
				"a", "alpha" -> 1
				"b", "beta" -> 2
				"rc" -> 4
				"" -> 8
				else -> 0
			}

		fun parse(versionName: String): VersionId {
			val parts = versionName.substringBeforeLast('-').split('.')
			val variant = versionName.substringAfterLast('-', "")
			return VersionId(
				major = parts.getOrNull(0)?.toIntOrNull() ?: 0,
				minor = parts.getOrNull(1)?.toIntOrNull() ?: 0,
				build = parts.getOrNull(2)?.toIntOrNull() ?: 0,
				variantType = variant.filter(Char::isLetter),
				variantNumber = variant.filter(Char::isDigit).toIntOrNull() ?: 0
			)
		}
	}
}